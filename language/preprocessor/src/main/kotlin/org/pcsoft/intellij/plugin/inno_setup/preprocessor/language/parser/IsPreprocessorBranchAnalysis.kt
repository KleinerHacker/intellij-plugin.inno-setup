/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.jetbrains.annotations.VisibleForTesting
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.*
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import java.util.concurrent.atomic.AtomicLong

/**
 * Whether a conditional branch is compiled, skipped, or cannot be decided statically.
 *
 * ISPP is an interpreter with side effects (`#emit`, `#expr`, `#for`, `Exec`, `FileRead`, `GetEnv`, …), so
 * deciding which branch a build actually takes is in general impossible at edit time. [UNKNOWN] is therefore
 * the honest default, not an error: only [INACTIVE] is ever greyed out or dropped.
 */
enum class IsBranchState {
    /** The branch is provably compiled. */
    ACTIVE,

    /** The branch is provably skipped — the only state that gets greyed out / pruned. */
    INACTIVE,

    /** The condition could not be decided statically; both this and the sibling branches are kept. */
    UNKNOWN,
}

/** A condition that could not be evaluated statically, with the host range of its expression. */
data class IsBranchUnknownReason(
    /** Host-file range of the condition expression the marker is attached to. */
    val range: TextRange,
    /** Why the condition could not be decided, appended to the user-visible message. */
    val message: String,
)

/**
 * Result of the conditional-branch analysis of one host file.
 *
 * Branch states are keyed by the **host offset of the preprocessor line** carrying the branch directive
 * (the same key [isppDirectivesWithHostOffset] produces) rather than by PSI instance, so a cached analysis
 * never keeps PSI alive.
 */
class IsBranchAnalysis(
    /** State per branch directive, keyed by the host offset of its preprocessor line. */
    val states: Map<Int, IsBranchState>,
    /** Host ranges of the bodies of provably inactive branches, excluding the directive lines themselves. */
    val inactiveRanges: List<TextRange>,
    /** Conditions that could not be evaluated statically. */
    val unknown: List<IsBranchUnknownReason>,
    /**
     * Host ranges of the `#if`/`#elif`/`#else`/`#endif` lines of blocks in which **every** branch was
     * decided. Only such a block can be collapsed away entirely when materialising an effective script; a
     * block containing an undecidable branch has to keep its directives, because which branch survives is
     * exactly what is unknown.
     */
    val decidedDirectiveLines: List<TextRange> = emptyList(),
    /** Host start offsets of the opener line of every block that contains an undecidable branch. */
    val undecidedBlockOffsets: List<Int> = emptyList(),
) {
    /** State of the branch directive on the preprocessor line starting at [hostOffset]. */
    fun stateOf(hostOffset: Int): IsBranchState = states[hostOffset] ?: IsBranchState.UNKNOWN

    /** Whether [offset] lies inside the body of a provably inactive branch. */
    fun isInactive(offset: Int): Boolean = inactiveRanges.any { it.containsOffset(offset) }

    /**
     * Whether [range] lies **entirely** inside the body of a provably inactive branch.
     *
     * Containment, not overlap: a section that merely *contains* a dead `#if` branch is itself alive, and
     * suppressing its diagnostics because one branch inside it is dead would hide real problems.
     */
    fun isInactive(range: TextRange): Boolean = inactiveRanges.any { it.contains(range) }

    companion object {
        /** An analysis that decided nothing — used whenever the host file is unavailable. */
        val EMPTY = IsBranchAnalysis(emptyMap(), emptyList(), emptyList())
    }
}

/**
 * Evaluates the `#if`/`#elif`/`#else`/`#endif` (and `#ifdef`/`#ifndef`/`#ifexist`/`#ifnexist`) structure of a
 * host file to decide, per branch, whether it is compiled.
 *
 * **Why a dedicated sequential pass.** [IsPreprocessorExprValueResolver] scopes declarations purely by
 * `order < beforeOrder` (the host offset of the declaring line). That models straight-line flow and is wrong
 * under conditional compilation: a `#define` inside a skipped branch does not exist at all, and one inside an
 * undecidable branch makes the symbol undecidable from there on. So this pass walks the directives **once** in
 * document order carrying a mutable [Env], and only applies a mutation while the surrounding branch is
 * [IsBranchState.ACTIVE].
 *
 * The expression evaluation itself is delegated to the existing [IsPreprocessorExprEvaluator]; only the
 * resolution lambdas are re-pointed from the order-filtered declaration lists to the live [Env].
 */
object IsPreprocessorBranchAnalysis {

    /**
     * Number of conditions evaluated since JVM start. Purely a test observable: it makes cache effectiveness
     * and per-run work assertable without measuring wall-clock time (see the branch-analysis cache tests).
     */
    @VisibleForTesting
    val evaluatedConditions = AtomicLong(0)

    /**
     * The analysis of the host file [file] belongs to.
     *
     * [file] may be the injected ISPP fragment rather than the script itself — that is what an annotator or a
     * test fixture with the caret inside a `#…` line hands out — so the top-level host is resolved first. All
     * offsets in the result are host-file offsets.
     *
     * Cached per document revision. A highlighting pass calls this once per annotated element and from
     * several threads at a time, so the caching must be both correct under concurrency and stable across the
     * pass — see the dependency choice below.
     */
    fun analyze(file: PsiFile): IsBranchAnalysis {
        val host = InjectedLanguageManager.getInstance(file.project).getTopLevelFile(file).asIsppHostFile()
            ?: return IsBranchAnalysis.EMPTY

        return CachedValuesManager.getCachedValue(host) {
            // Depend on the *document*, not on the PSI file and not on PsiModificationTracker.
            // MODIFICATION_COUNT: both of those also react to the ISPP injections that the highlighting pass
            // materialises while it runs, which invalidated the entry mid-pass and had a single pass over a
            // two-directive file recompute the analysis four times. The document changes only when the text
            // does — exactly what the result depends on.
            //
            // The symbols seeded by seedExternalSymbols() are the part that does *not* depend on the text, so
            // IsPreprocessorSymbolTracker is listed as a second dependency: switching the build configuration
            // must flip the greyed-out branches without the file being touched.
            val dependency: Any = host.viewProvider.document ?: PsiModificationTracker.MODIFICATION_COUNT
            CachedValueProvider.Result.create(compute(host), dependency, IsPreprocessorSymbolTracker.getInstance(host.project))
        }
    }

    // ── environment ────────────────────────────────────────────────────────────

    /** What is known about one preprocessor symbol at a given point of the walk. */
    private sealed interface Sym {
        /** Defined with a statically computed value. */
        data class Known(val value: IsPreprocessorExprValue) : Sym

        /** Definitely defined, but the value is not computable (predefined variable, impure call, …). */
        data object DefinedUnknownValue : Sym

        /** May or may not be defined — assigned inside an undecidable branch. */
        data object MaybeDefined : Sym
    }

    /**
     * The symbol table as it stands at one point of the walk. A name **absent** from [symbols] is undefined,
     * which ISPP evaluates as `0` in a condition (the same degradation [IS_PREPROCESSOR_BOOLEAN_WORDS]
     * documents for `true`/`false`).
     *
     * [poisoned] is set once a directive or call is reached whose effect on the symbol table cannot be
     * modelled (`#expr`, `#emit`, `#for`, a call to an impure builtin, …). From then on every condition is
     * [IsBranchState.UNKNOWN] — the analysis stops claiming knowledge rather than guessing.
     */
    private class Env {
        val symbols = HashMap<String, Sym>()
        var poisoned: Boolean = false

        fun key(name: String) = name.lowercase()
        fun get(name: String): Sym? = symbols[key(name)]
        fun define(name: String, sym: Sym) = symbols.put(key(name), sym)
        fun undefine(name: String) = symbols.remove(key(name))
    }

    // ── the walk ───────────────────────────────────────────────────────────────

    /** One open `#if` block while walking. */
    private class Frame(
        /** State of the context enclosing the whole block. */
        val enclosing: IsBranchState,
        /** State of the branch currently being walked. */
        var current: IsBranchState = IsBranchState.UNKNOWN,
        /** A previous branch of this block is provably taken, so every later branch is skipped. */
        var anyBranchTaken: Boolean = false,
        /** Every previous branch of this block is provably skipped — the next one can still be decided. */
        var allPreviousFalse: Boolean = true,
        /** The block already has its `#else`. */
        var sawElse: Boolean = false,
    ) {
        /** Names `#define`d inside an undecidable branch of this block. */
        val maybeDefined = mutableSetOf<String>()

        /** Names `#undef`ed inside an undecidable branch of this block. */
        val maybeUndefined = mutableSetOf<String>()
    }

    private fun compute(hostFile: PsiFile): IsBranchAnalysis {
        // Collect the directives (and their host lines) exactly once: enumerating the ISPP injection of every
        // preprocessor line dominates the cost on a large script, and doing it a second time for the
        // conditional structure made the whole analysis scale far worse than the directive count alone.
        val directivesWithLine = preprocessorDirectivesWithHostLine(hostFile)
        if (directivesWithLine.isEmpty()) return IsBranchAnalysis.EMPTY
        val directives = directivesWithLine.map { (directive, line) -> directive to line.textRange.startOffset }

        val env = Env()
        seedPredefined(env)
        seedExternalSymbols(env, hostFile)

        val states = HashMap<Int, IsBranchState>()
        val unknown = mutableListOf<IsBranchUnknownReason>()
        val stack = ArrayDeque<Frame>()
        val subNames = mutableSetOf<String>()

        for ((directive, offset) in directives) {
            val ex = directive as? IsPreprocessorDirectiveEx ?: continue
            val enclosing = stack.lastOrNull()?.current ?: IsBranchState.ACTIVE

            when {
                ex.isConditionalOpener() -> {
                    val frame = Frame(enclosing = enclosing)
                    stack.addLast(frame)
                    applyBranch(frame, ex, directive, offset, env, states, unknown, subNames)
                }

                ex.isElif() || ex.isElse() -> {
                    // A stray #elif/#else without an open block is a structural error reported elsewhere;
                    // here it simply has no frame to update.
                    val frame = stack.lastOrNull() ?: continue
                    if (ex.isElse()) frame.sawElse = true
                    applyBranch(frame, ex, directive, offset, env, states, unknown, subNames)
                }

                ex.isEndif() -> {
                    val frame = stack.removeLastOrNull() ?: continue
                    closeFrame(frame, env)
                }

                // Only mutate the symbol table for branches that are provably compiled; record the name as
                // undecidable while inside an undecidable branch; ignore it entirely when provably skipped.
                else -> applyDirective(ex, env, stack.lastOrNull(), enclosing, subNames)
            }
        }

        val blocks = IsPreprocessorConditionalStructure.structureOf(directivesWithLine).blocks
        return IsBranchAnalysis(
            states = states,
            inactiveRanges = inactiveRangesOf(blocks, states),
            unknown = unknown,
            decidedDirectiveLines = decidedDirectiveLinesOf(blocks, states),
            undecidedBlockOffsets = blocks.filter { !isDecided(it, states) }
                .map { it.openerLine.textRange.startOffset },
        )
    }

    /** Whether every branch of [block] came out as either provably active or provably inactive. */
    private fun isDecided(block: IsConditionalBlock, states: Map<Int, IsBranchState>): Boolean =
        block.branches.all { states[it.line.textRange.startOffset].let { s -> s != null && s != IsBranchState.UNKNOWN } }

    /**
     * Computes and records the state of one branch (`#if`/`#ifdef`/`#ifexist` opener, `#elif` or `#else`) and
     * makes it the frame's current branch.
     */
    private fun applyBranch(
        frame: Frame,
        ex: IsPreprocessorDirectiveEx,
        directive: IsPreprocessorDirective,
        offset: Int,
        env: Env,
        states: MutableMap<Int, IsBranchState>,
        unknown: MutableList<IsBranchUnknownReason>,
        subNames: Set<String>,
    ) {
        val state = when {
            // Anything nested inside a skipped or undecidable branch inherits that state — its own condition
            // is never even evaluated by the real preprocessor.
            frame.enclosing != IsBranchState.ACTIVE -> frame.enclosing
            frame.anyBranchTaken -> IsBranchState.INACTIVE
            // Some earlier branch of this block was undecidable, so we cannot know whether this one runs.
            !frame.allPreviousFalse -> IsBranchState.UNKNOWN
            ex.isElse() -> IsBranchState.ACTIVE // all previous branches provably false
            else -> when (val decided = evaluateBranchCondition(ex, directive, env, unknown, subNames)) {
                true -> IsBranchState.ACTIVE
                false -> IsBranchState.INACTIVE
                null -> IsBranchState.UNKNOWN
            }
        }

        states[offset] = state
        frame.current = state
        when (state) {
            IsBranchState.ACTIVE -> frame.anyBranchTaken = true
            IsBranchState.UNKNOWN -> frame.allPreviousFalse = false
            IsBranchState.INACTIVE -> Unit // stays "all previous false" so a later branch can still decide
        }
    }

    /** Merges the undecidable assignments of a finished block back into the enclosing environment. */
    private fun closeFrame(frame: Frame, env: Env) {
        frame.maybeUndefined.forEach { env.define(it, Sym.MaybeDefined) }
        frame.maybeDefined.forEach { name ->
            // Already defined before the block and possibly redefined inside → still definitely defined,
            // but the value is no longer known. Otherwise its very existence is undecidable.
            val previous = env.get(name)
            val merged = if (previous != null && name !in frame.maybeUndefined) Sym.DefinedUnknownValue
            else Sym.MaybeDefined
            env.define(name, merged)
        }
    }

    /**
     * Applies a non-conditional directive to [env]. Mutations take effect only in a provably active context;
     * inside an undecidable branch the touched name is recorded on [frame] and merged at `#endif`; inside a
     * provably skipped branch the directive is ignored, exactly as the real preprocessor would.
     */
    private fun applyDirective(
        ex: IsPreprocessorDirectiveEx,
        env: Env,
        frame: Frame?,
        enclosing: IsBranchState,
        subNames: MutableSet<String>,
    ) {
        if (ex.isSub()) {
            ex.getSubroutineName()?.let { subNames += it.lowercase() }
            return
        }

        // Directives that execute arbitrary preprocessor code and can assign to any name. Modelling them
        // would mean interpreting ISPP; instead the symbol table stops being trusted from here on.
        if (ex.isFor() || ex.isPragma() || ex.keywordIsOneOf("emit", "expr", "insert", "append")) {
            env.poisoned = true
            return
        }

        if (enclosing == IsBranchState.INACTIVE) return

        val undecidable = enclosing == IsBranchState.UNKNOWN

        when {
            // #undef carries its name as the directive's name identifier, not as a #define name.
            ex.isUndef() -> {
                val name = ex.nameIdentifier?.text?.trim()?.ifEmpty { null } ?: return
                if (undecidable) frame?.maybeUndefined?.add(name.lowercase()) else env.undefine(name)
            }

            ex.isArrayDeclaration() -> {
                val name = ex.getArrayName() ?: return
                if (undecidable) frame?.maybeDefined?.add(name.lowercase())
                else env.define(name, Sym.DefinedUnknownValue)
            }

            ex.isDefine() -> {
                // An array element assignment (`#define Name[0] …`) is a usage of an existing array, not a
                // declaration of a new symbol.
                if (ex.isArrayElementDefine()) return
                val name = ex.getDefineName() ?: return
                if (undecidable) {
                    frame?.maybeDefined?.add(name.lowercase())
                    return
                }
                if (ex.isFunctionMacro()) {
                    env.define(name, Sym.DefinedUnknownValue)
                    return
                }
                val expr = ex.getDefineExpressionText()
                val value = if (expr.isNullOrBlank()) null else evaluateExpression(expr, env, subNames)
                env.define(name, if (value == null) Sym.DefinedUnknownValue else Sym.Known(value))
            }
        }
    }

    // ── condition evaluation ───────────────────────────────────────────────────

    /**
     * Decides the condition of [ex]: `true`/`false` when it is provably decided, `null` when it is not — in
     * which case an [IsBranchUnknownReason] is recorded for the annotator.
     */
    private fun evaluateBranchCondition(
        ex: IsPreprocessorDirectiveEx,
        directive: IsPreprocessorDirective,
        env: Env,
        unknown: MutableList<IsBranchUnknownReason>,
        subNames: Set<String>,
    ): Boolean? {
        evaluatedConditions.incrementAndGet()

        fun undecidable(reason: String): Boolean? {
            conditionRange(directive, ex)?.let { unknown += IsBranchUnknownReason(it, reason) }
            return null
        }

        if (env.poisoned) {
            return undecidable("the preprocessor state is no longer statically known at this point")
        }

        // #ifdef / #ifndef take a bare identifier and ask about definedness, not about a value. Their argument
        // is the directive's plain value, not a condition expression.
        if (ex.isIfdefFamily()) {
            val name = directive.value?.text?.trim()
            if (name.isNullOrEmpty()) return undecidable("no symbol name is given")
            val defined = definedness(name, env) ?: return undecidable("'$name' may or may not be defined here")
            return if (ex.isIfndef()) !defined else defined
        }

        // #ifexist / #ifnexist take a file name, not a condition expression — like #ifdef their argument is
        // the directive's plain value. They are decidable whenever that file name is: the include
        // infrastructure resolves it exactly like a real #include.
        if (ex.isIfExistFamily()) {
            val raw = directive.value?.text?.trim()
            if (raw.isNullOrEmpty()) return undecidable("no file name is given")
            // A literal path resolves directly; anything else is a string expression that must first be
            // computed against the current symbol table (e.g. `#ifexist MyFile` or `#ifexist Dir + "\a.iss"`).
            val path = ex.getExistPath() ?: run {
                val value = evaluateExpression(raw, env, subNames)
                    ?: return undecidable(reasonFor(raw, env, subNames))
                (value as? IsPreprocessorExprValue.StrValue)?.value
                    ?: return undecidable("the file name does not evaluate to a string")
            }
            val exists = ex.resolveRelativeFile(path) != null
            return if (ex.isIfNexist()) !exists else exists
        }

        val text = ex.getConditionExpressionText()?.trim()
        if (text.isNullOrEmpty()) return undecidable("the condition is empty")

        val value = evaluateExpression(text, env, subNames)
            ?: return undecidable(reasonFor(text, env, subNames))
        val int = (value as? IsPreprocessorExprValue.IntValue)
            ?: return undecidable("the condition evaluates to a string, not to a truth value")
        return int.value != 0L
    }

    /**
     * Statically evaluates [text] against [env], or returns `null` when it is not computable. Handles
     * `defined(X)` itself — the generic evaluator only ever sees argument *values*, so it could not answer a
     * question about definedness — and poisons [env] on a call that has side effects.
     */
    private fun evaluateExpression(text: String, env: Env, subNames: Set<String>): IsPreprocessorExprValue? {
        if (env.poisoned) return null

        val parsed = IsPreprocessorExprParser.parse(text)
        if (parsed.errors.isNotEmpty()) return null

        val calls = mutableListOf<IsPreprocessorExprCall>()
        collectCalls(parsed.ast, calls)

        // A call into user code or into an impure builtin can change the symbol table in ways this analysis
        // cannot follow, so it invalidates everything that comes after it.
        if (calls.any { it.name.lowercase() in subNames || isImpureBuiltin(it.name) }) {
            env.poisoned = true
            return null
        }

        // Substitute every defined(X) with its truth value, right to left so earlier spans stay valid.
        var effective = text
        val definedCalls = calls.filter { it.name.equals("defined", ignoreCase = true) }
        for (call in definedCalls.sortedByDescending { it.span.start }) {
            val argument = call.arguments.singleOrNull() as? IsPreprocessorExprReference ?: return null
            val defined = definedness(argument.name, env) ?: return null
            effective = effective.replaceRange(call.span.start, call.span.end, if (defined) "1" else "0")
        }

        val ast = if (definedCalls.isEmpty()) parsed.ast else {
            val reparsed = IsPreprocessorExprParser.parse(effective)
            if (reparsed.errors.isNotEmpty()) return null
            reparsed.ast
        }

        return IsPreprocessorExprEvaluator(
            text = effective,
            referenceValue = { name -> referenceValue(name, env) },
        ).evaluate(ast)
    }

    /**
     * Value of a plain reference. An **undefined** name is `0` — that is what ISPP itself does with an unknown
     * identifier in an expression, so it is a decided value, not a gap.
     */
    private fun referenceValue(name: String, env: Env): IsPreprocessorExprValue? = when (val sym = env.get(name)) {
        is Sym.Known -> sym.value
        Sym.DefinedUnknownValue, Sym.MaybeDefined -> null
        null -> IsPreprocessorExprValue.IntValue(0)
    }

    /** Whether [name] is defined at this point: `null` when that itself is undecidable. */
    private fun definedness(name: String, env: Env): Boolean? = when (env.get(name)) {
        is Sym.Known, Sym.DefinedUnknownValue -> true
        Sym.MaybeDefined -> null
        null -> false
    }

    /** A short, user-facing explanation of why [text] could not be decided. */
    private fun reasonFor(text: String, env: Env, subNames: Set<String>): String {
        val references = mutableListOf<IsPreprocessorExprReference>()
        val calls = mutableListOf<IsPreprocessorExprCall>()
        val ast = IsPreprocessorExprParser.parse(text).ast
        collectReferences(ast, references)
        collectCalls(ast, calls)

        calls.firstOrNull { isImpureBuiltin(it.name) }
            ?.let { return "'${it.name}' reads state that is only known while compiling" }
        calls.firstOrNull { it.name.lowercase() in subNames }
            ?.let { return "'${it.name}' is a preprocessor subroutine and is not evaluated here" }
        calls.firstOrNull()
            ?.let { return "the result of '${it.name}' is not statically computable" }
        references.firstOrNull { env.get(it.name) != null && env.get(it.name) !is Sym.Known }
            ?.let { return "the value of '${it.name}' is not statically known" }
        return "it is not statically computable"
    }

    private fun collectCalls(node: IsPreprocessorExprNode, into: MutableList<IsPreprocessorExprCall>) {
        if (node is IsPreprocessorExprCall) into += node
        children(node).forEach { collectCalls(it, into) }
    }

    private fun collectReferences(node: IsPreprocessorExprNode, into: MutableList<IsPreprocessorExprReference>) {
        if (node is IsPreprocessorExprReference) into += node
        children(node).forEach { collectReferences(it, into) }
    }

    private fun children(node: IsPreprocessorExprNode): List<IsPreprocessorExprNode> = when (node) {
        is IsPreprocessorExprCall -> node.arguments
        is IsPreprocessorExprIndex -> listOf(node.index)
        is IsPreprocessorExprParen -> listOf(node.inner)
        is IsPreprocessorExprUnary -> listOf(node.operand)
        is IsPreprocessorExprBinary -> listOf(node.left, node.right)
        is IsPreprocessorExprTernary -> listOf(node.condition, node.whenTrue, node.whenFalse)
        else -> emptyList()
    }

    private fun isImpureBuiltin(name: String): Boolean =
        service<IsPreprocessorService>().spec.builtinFunctions
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.pure == false

    // ── seeding ────────────────────────────────────────────────────────────────

    /**
     * Seeds the predefined ISPP symbols. They are all *defined* — including the valueless `void` ones
     * (`__WIN32__`, `ISPP_INVOKED`, `WINDOWS`, `UNICODE`, …), which exist precisely to be tested with
     * `#ifdef` — but their values are decided by the compiler, not by the IDE.
     */
    private fun seedPredefined(env: Env) {
        service<IsPreprocessorService>().spec.predefinedVariables.forEach {
            env.define(it.name, Sym.DefinedUnknownValue)
        }
    }

    /** Seeds symbols contributed from outside the script, e.g. ISCC `/D…` arguments of a run configuration. */
    private fun seedExternalSymbols(env: Env, hostFile: PsiFile) {
        hostFile.externalPreprocessorSymbols().forEach { (name, value) ->
            val parsed = value?.trim()?.toLongOrNull()
            env.define(
                name,
                if (parsed != null) Sym.Known(IsPreprocessorExprValue.IntValue(parsed))
                else Sym.DefinedUnknownValue,
            )
        }
    }

    // ── ranges ─────────────────────────────────────────────────────────────────

    /**
     * The host ranges covered by the bodies of provably inactive branches: from the end of the branch
     * directive line up to the start of the next branch (or the `#endif`). The directive lines themselves stay
     * out, so `#if`/`#else`/`#endif` keep their normal highlighting around greyed-out content.
     */
    private fun inactiveRangesOf(
        blocks: List<IsConditionalBlock>,
        states: Map<Int, IsBranchState>,
    ): List<TextRange> {
        val ranges = mutableListOf<TextRange>()
        blocks.forEach { block ->
            block.branches.forEachIndexed { index, branch ->
                if (states[branch.line.textRange.startOffset] != IsBranchState.INACTIVE) return@forEachIndexed
                val end = block.branches.getOrNull(index + 1)?.line ?: block.endifLine
                val start = branch.line.textRange.endOffset
                if (end.textRange.startOffset > start) {
                    ranges += TextRange(start, end.textRange.startOffset)
                }
            }
        }
        return ranges
    }

    /** The directive lines of every fully decided block — the parts an effective script can collapse away. */
    private fun decidedDirectiveLinesOf(
        blocks: List<IsConditionalBlock>,
        states: Map<Int, IsBranchState>,
    ): List<TextRange> = blocks.filter { isDecided(it, states) }
        .flatMap { block -> block.branches.map { it.line.textRange } + block.endifLine.textRange }

    /**
     * Host range of the condition of [ex], used to anchor the "not evaluable" marker.
     *
     * The directive lives in the injected ISPP fragment, so its own offsets are fragment-relative;
     * [InjectedLanguageManager.injectedToHost] maps them onto the script the marker is painted in.
     */
    private fun conditionRange(directive: IsPreprocessorDirective, ex: IsPreprocessorDirectiveEx): TextRange? {
        val injected = if (ex.isIfdefFamily() || ex.isIfExistFamily()) {
            directive.value?.textRange?.takeUnless { it.isEmpty }
        } else {
            val text = ex.getConditionExpressionText()?.takeUnless { it.isEmpty() }
            text?.let {
                val start = directive.textRange.startOffset + ex.getConditionExpressionOffsetInDirective()
                TextRange(start, start + it.length)
            }
        } ?: directive.textRange

        return InjectedLanguageManager.getInstance(directive.project).injectedToHost(directive, injected)
    }
}

/** Whether this directive's keyword is one of [keywords], matched case-insensitively. */
private fun IsPreprocessorDirectiveEx.keywordIsOneOf(vararg keywords: String): Boolean {
    val keyword = (this as? IsPreprocessorDirective)?.identifier?.text ?: return false
    return keywords.any { it.equals(keyword, ignoreCase = true) }
}
