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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.PlatformAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorExpressionReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorForVariableReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorMacroParameterReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.*
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.RemoveIncludeQuickFix
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.RemoveUnusedDefineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.RemoveUselessUndefQuickFix
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.ReplaceIncludeWithLineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorPragmaArgument
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorPragmaSpec

/**
 * Annotates Inno Setup PSI elements with validation and highlighting information.
 */
class IsPreprocessorAnnotator : Annotator {

    private companion object {
        /** Token kinds that are painted with the operator colour inside #define expressions. */
        val OPERATOR_TOKEN_TYPES = setOf(
            IsPreprocessorExprTokenType.OPERATOR,
            IsPreprocessorExprTokenType.LPAREN,
            IsPreprocessorExprTokenType.RPAREN,
            IsPreprocessorExprTokenType.LBRACKET,
            IsPreprocessorExprTokenType.RBRACKET,
            IsPreprocessorExprTokenType.COMMA,
            IsPreprocessorExprTokenType.QUESTION,
            IsPreprocessorExprTokenType.COLON,
        )

        /** A single `#pragma option`/`parseroption` flag, e.g. `-v+` or `-c-`. */
        val PRAGMA_FLAG = Regex("^-([A-Za-z])([+-])$")

        /** Splits a flag argument into its whitespace-separated tokens (with their positions). */
        val NON_WHITESPACE = Regex("\\S+")

        /** A single ISPP identifier (the `#ifdef`/`#ifndef` argument), mirroring the lexer's IDENTIFIER rule. */
        val IDENTIFIER = Regex("[A-Za-z_][A-Za-z0-9_.\\-]*")

        /** A `#for` increment/decrement statement: `i++`, `i--`, `++i` or `--i`. */
        val FOR_INCREMENT = Regex("""(\+\+|--)\s*[A-Za-z_]\w*|[A-Za-z_]\w*\s*(\+\+|--)""")

        /** A valid `#sub` name: a plain identifier (letters/underscore start, then letters/digits/underscore). */
        val SUBROUTINE_NAME = Regex("^[A-Za-z_][A-Za-z0-9_]*$")
    }

    /**
     * Annotates the supplied PSI element when it matches this component's checks.
     */
    override fun annotate(element: PsiElement, annotationHolder: AnnotationHolder) =
        annotate(element, PlatformAnnotationSink(annotationHolder))

    fun annotate(element: PsiElement, holder: IsAnnotationSink) {
        // Token-level syntax highlighting (strings/numbers). Inside the ISPP injection the injected
        // SyntaxHighlighter lexer does not paint reliably in the host editor, so the colours are applied
        // here through the annotator pass — the same path the directive keyword highlighting uses.
        when (element.node?.elementType) {
            IsPreprocessorTypes.QUOTE, IsPreprocessorTypes.STRING_PART ->
                highlight(element.textRange, IsPreprocessorSyntaxHighlighting.STRING, holder)

            IsPreprocessorTypes.NUMBER ->
                highlight(element.textRange, IsPreprocessorSyntaxHighlighting.NUMBER, holder)
        }

        if (element is IsPreprocessorDirective) {
            // A directive inside a provably skipped branch is never executed, so reporting problems in it
            // would put red squiggles into greyed-out dead code. The conditional *structure* is the sole
            // exception: unbalanced #if/#endif nesting breaks the file no matter which branch is compiled,
            // so it is still validated below.
            val hostRange = InjectedLanguageManager.getInstance(element.project)
                .injectedToHost(element, element.textRange)
            if (IsPreprocessorBranchAnalysis.analyze(element.containingFile).isInactive(hostRange)) {
                annotateConditionalStructure(element, holder)
                return
            }

            annotateDirective(element, holder)
        }
    }

    private fun annotateDirective(directive: IsPreprocessorDirective, holder: IsAnnotationSink) {
        val hash = directive.node.findChildByType(IsPreprocessorTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsPreprocessorTypes.IDENTIFIER) ?: return
        val keywordRange = TextRange(hash.startOffset, keyword.textRange.endOffset)

        // The directive keyword must be one declared by the ISPP spec (the single source of truth).
        // ISPP directives are case-insensitive.
        val known = service<IsPreprocessorService>().spec.directives
            .any { it.name.equals(keyword.text, ignoreCase = true) }
        if (!known) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown preprocessor directive: '#${keyword.text}'")
                .range(keywordRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }

        highlight(keywordRange, IsPreprocessorAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

        val ex = directive as? IsPreprocessorDirectiveEx ?: return

        if (ex.isInclude()) {
            annotateInclude(directive, ex, holder)
            return
        }
        if (ex.isPragma()) {
            annotatePragma(directive, ex, holder)
            return
        }
        if (ex.isUndef()) {
            annotateUndef(directive, ex, holder)
            return
        }
        if (ex.isConditionalDirective()) {
            annotateConditional(directive, ex, holder)
            return
        }
        if (ex.isSubroutineDirective()) {
            annotateSubroutine(directive, ex, holder)
            return
        }
        if (ex.isFor()) {
            annotateFor(directive, ex, holder)
            return
        }
        if (ex.isArrayDeclaration()) {
            annotateArrayDeclaration(directive, ex, holder)
            return
        }
        if (!ex.isDefine()) return

        // An array element assignment `#define Name[Index] Value` has its own validation (array existence,
        // index type, bounds) and does not participate in the scalar #define checks below.
        if (ex.isArrayElementDefine()) {
            annotateArrayElementDefine(directive, ex, holder)
            return
        }

        // Optional scope/visibility keyword (#define public Foo …) — highlighted like a keyword.
        highlightVisibility(ex, holder)

        // A #define name must start with a letter/underscore. When it starts with a digit the lexer splits
        // it into NUMBER + IDENTIFIER, so the first value token is a NUMBER — flag that as an error.
        digitLeadingNameRange(directive)?.let { range ->
            holder.newAnnotation(HighlightSeverity.ERROR, "A #define name must not start with a digit")
                .range(range)
                .create()
            return
        }

        val nameIdentifier = ex.nameIdentifier
        if (nameIdentifier != null) {
            // A macro name must not collide with a reserved ISPP keyword (see is-preprocessor.yaml).
            val forbidden = service<IsPreprocessorService>().spec.forbiddenVariableNames
                .firstOrNull { it.name.equals(nameIdentifier.text, ignoreCase = true) }
            if (forbidden != null) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'${nameIdentifier.text}' is a reserved preprocessor keyword and cannot be used as a #define name"
                ).range(nameIdentifier.textRange).create()
                return
            }
            highlight(nameIdentifier.textRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder)
        }

        // Identifiers in the expression that refer to a non-existent #define (and are not a known ISPP
        // built-in function or predefined variable) are unresolved references — flag them as errors.
        annotateUnresolvedReferences(directive, holder)

        // A function-like macro (#define Name(a,b) …) must have an expression body.
        if (ex.isFunctionMacro() && ex.getMacroBody() == null) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Function-like macro '${ex.getDefineName().orEmpty()}' requires an expression"
            ).range(directive.textRange).create()
            return
        }

        // Validate the #define expression: missing operators, syntax problems and type violations (e.g.
        // multiplying strings). Operators are highlighted here too. Reference types are resolved
        // recursively through the names of other #defines.
        annotateExpression(directive, ex, holder)

        val name = ex.getDefineName() ?: return
        if (!isDefineUsed(directive, name)) {
            // Gray only the name identifier, not the whole line — otherwise the value's syntax
            // highlighting (strings/numbers/operators) would be lost behind the unused colour.
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "#define '$name' is never used")
                .range(ex.nameIdentifier?.textRange ?: directive.textRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNUSED)
                .withFix(RemoveUnusedDefineQuickFix(directive))
                .create()
        }
    }

    /** Highlights the optional scope/visibility keyword of a `#define`/`#undef` like a keyword. */
    /**
     * Marks a condition the branch analysis could not decide statically.
     *
     * Deliberately a [HighlightSeverity.WEAK_WARNING] and not an error: an undecidable condition is normal
     * for a preprocessor that runs `Exec`, reads files or receives `/D` symbols on the ISCC command line. The
     * marker only explains why *both* branches stay lit instead of one being greyed out. Its severity also
     * keeps it out of the recorded-problem replay ([RecordingAnnotationBuilder] drops everything below
     * WARNING), so an `#include`d file does not report it a second time on its includer. The markup itself is
     * overridden to [IsPreprocessorAnnotatorHighlighting.UNDECIDABLE_CONDITION] — a blue wavy underline
     * instead of the yellow squiggle of a weak warning.
     */
    private fun annotateUndecidableCondition(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val injectedRange = conditionRangeInDirective(directive, ex) ?: return
        val manager = InjectedLanguageManager.getInstance(directive.project)
        val hostRange = manager.injectedToHost(directive, injectedRange)

        val reason = IsPreprocessorBranchAnalysis.analyze(directive.containingFile)
            .unknown.firstOrNull { it.range == hostRange } ?: return

        holder.newAnnotation(
            HighlightSeverity.WEAK_WARNING,
            "Condition cannot be evaluated statically — ${reason.message}; both branches are kept",
        ).range(injectedRange)
            .textAttributes(IsPreprocessorAnnotatorHighlighting.UNDECIDABLE_CONDITION)
            .create()
    }

    /** The condition range of [directive] in its own (injected) coordinates, or `null` when it has none. */
    private fun conditionRangeInDirective(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
    ): TextRange? {
        if (ex.isIfdefFamily() || ex.isIfExistFamily()) {
            return directive.value?.textRange?.takeUnless { it.isEmpty }
        }
        val text = ex.getConditionExpressionText()?.takeUnless { it.isEmpty() } ?: return null
        val start = directive.textRange.startOffset + ex.getConditionExpressionOffsetInDirective()
        return TextRange(start, start + text.length)
    }

    private fun highlightVisibility(ex: IsPreprocessorDirectiveEx, holder: IsAnnotationSink) {
        val visibility = ex.getVisibilityIdentifier() ?: return
        highlight(visibility.textRange, IsPreprocessorAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
    }

    /**
     * Validates an `#undef`: highlights the optional scope keyword, rejects a reserved keyword used as the
     * name, and resolves the name against the preceding `#define`s. A matching name is highlighted as a
     * define name; an `#undef` without any matching `#define` does nothing useful, so its name is grayed
     * out with a weak warning and a quick fix that removes the directive.
     */
    private fun annotateUndef(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        highlightVisibility(ex, holder)

        val nameIdentifier = ex.nameIdentifier ?: return

        val forbidden = service<IsPreprocessorService>().spec.forbiddenVariableNames
            .firstOrNull { it.name.equals(nameIdentifier.text, ignoreCase = true) }
        if (forbidden != null) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "'${nameIdentifier.text}' is a reserved preprocessor keyword and cannot be used as an #undef name"
            ).range(nameIdentifier.textRange).create()
            return
        }

        // Resolve against a preceding #define (declaration-order aware, via the directive's reference).
        val resolvedTarget = directive.references
            .filterIsInstance<IsPreprocessorExpressionReference>()
            .firstNotNullOfOrNull { it.resolve() }

        when {
            // An array declared with #dim/#redim is not a plain macro and cannot be removed with #undef.
            (resolvedTarget as? IsPreprocessorDirectiveEx)?.isDim() == true -> {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "#undef cannot be applied to the #dim array '${nameIdentifier.text}'"
                ).range(nameIdentifier.textRange).create()
            }

            resolvedTarget != null -> {
                highlight(nameIdentifier.textRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder)
            }

            else -> {
                holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    "#undef '${nameIdentifier.text}' has no matching #define"
                )
                    .range(nameIdentifier.textRange)
                    .textAttributes(IsPreprocessorAnnotatorHighlighting.UNUSED)
                    .withFix(RemoveUselessUndefQuickFix(directive))
                    .create()
            }
        }
    }

    /**
     * Validates a conditional directive (`#if`/`#elif`/`#else`/`#endif` and the `#ifdef`-family): checks the
     * block structure (every opener must be closed by `#endif`; no stray `#elif`/`#else`/`#endif`); for
     * `#if`/`#elif` it additionally analyses the condition expression (operators, syntax/type errors,
     * references) and marks a literal boolean (`true`/`false`/`yes`/`no`) yellow with a warning, because
     * ISPP has no booleans and would treat the word as an undefined identifier (0).
     */
    private fun annotateConditional(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        annotateConditionalStructure(directive, holder)
        annotateUndecidableCondition(directive, ex, holder)
        annotateConditionalArgument(directive, ex, holder)
    }

    /**
     * Reports an unbalanced conditional: an opener without `#endif`, a branch without an opener, or an `#elif`
     * after `#else`.
     *
     * Runs even for directives inside a provably skipped branch — a malformed `#if`/`#endif` nesting breaks
     * the whole file regardless of which branch the compiler would take, so this is the one check that must
     * never be suppressed as "dead code".
     */
    private fun annotateConditionalStructure(directive: IsPreprocessorDirective, holder: IsAnnotationSink) {
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        if (hostFile != null) {
            val problem = IsPreprocessorConditionalStructure.structureOf(hostFile).problems[directive]
            if (problem != null) {
                val keywordNode = directive.identifier
                val keyword = keywordNode?.text ?: "if"
                val message = when (problem) {
                    IsConditionalProblem.UnterminatedOpener -> "Unterminated #$keyword: missing #endif"
                    IsConditionalProblem.ElifAfterElse -> "#elif cannot appear after #else"
                    IsConditionalProblem.StrayBranch -> "#$keyword without matching #if"
                }
                val hash = directive.node.findChildByType(IsPreprocessorTypes.HASH)
                val range = if (keywordNode != null)
                    TextRange(hash?.startOffset ?: keywordNode.textRange.startOffset, keywordNode.textRange.endOffset)
                else directive.textRange
                holder.newAnnotation(HighlightSeverity.ERROR, message).range(range).create()
            }
        }
    }

    /** Validates the argument of a conditional directive: an identifier, a file name or an expression. */
    private fun annotateConditionalArgument(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()

        // ── #ifdef / #ifndef: a single identifier naming a (possibly non-existent) #define ──
        if (ex.isIfdefFamily()) {
            val keyword = directive.identifier?.text ?: "ifdef"
            val value = directive.value
            val argument = value?.text?.trim()
            if (value == null || argument.isNullOrEmpty()) {
                holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires an identifier")
                    .range(directive.textRange).create()
                return
            }
            // The argument must be exactly one identifier — an expression, a literal or several tokens
            // (e.g. `Foo+Bar`, `1`, `"x"`, `Foo Bar`) is not a macro name and cannot be tested.
            if (!IDENTIFIER.matches(argument)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires a single identifier")
                    .range(value.textRange).create()
                return
            }
            // Highlight the name like a define only when it resolves; a missing #define is legitimate here
            // (no error, unlike an unresolved reference in a #if condition).
            val ref = directive.references.filterIsInstance<IsPreprocessorExpressionReference>().firstOrNull()
            if (ref?.resolve() != null) {
                highlight(
                    ref.rangeInElement.shiftRight(directive.textRange.startOffset),
                    IsPreprocessorAnnotatorHighlighting.DEFINE_NAME,
                    holder,
                )
            }
            return
        }

        // ── #ifexist / #ifnexist: a string expression naming a file (existence not yet checked) ──
        if (ex.isIfExistFamily()) {
            val keyword = directive.identifier?.text ?: "ifexist"
            val value = directive.value
            if (value == null || value.text.isBlank()) {
                holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires a string value")
                    .range(directive.textRange).create()
                return
            }

            val exprText = value.text
            val base = value.textRange.startOffset
            val tokens = IsPreprocessorExprTokenizer.tokenize(exprText)
            tokens.filter { it.type in OPERATOR_TOKEN_TYPES }.forEach { token ->
                highlight(
                    TextRange(base + token.start, base + token.end),
                    IsPreprocessorSyntaxHighlighting.OPERATOR,
                    holder,
                )
            }

            val parseResult = IsPreprocessorExprParser.parse(tokens, exprText.length)
            val resolver = buildTypeResolver(hostFile)
            val inference = resolver.inferenceAt(currentDirectiveOrder(directive, hostFile))
            val type = inference.infer(parseResult.ast)

            (parseResult.errors + inference.errors).forEach { error ->
                holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                    .range(TextRange(base + error.span.start, base + error.span.end))
                    .create()
            }

            annotateUnresolvedReferences(directive, holder)

            if (!type.strCompatible) {
                holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires a string value")
                    .range(value.textRange).create()
            }
            return
        }

        if (!ex.isIfElif()) return

        // ── condition expression ──
        // #if/#elif require a condition — an empty one is invalid.
        val exprText = ex.getConditionExpressionText()
        val exprOffset = ex.getConditionExpressionOffsetInDirective()
        if (exprText.isNullOrBlank() || exprOffset < 0) {
            val keyword = directive.identifier?.text ?: "if"
            holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires a condition expression")
                .range(directive.textRange).create()
            return
        }
        val base = directive.textRange.startOffset + exprOffset

        val tokens = IsPreprocessorExprTokenizer.tokenize(exprText)
        tokens.filter { it.type in OPERATOR_TOKEN_TYPES }.forEach { token ->
            highlight(
                TextRange(base + token.start, base + token.end),
                IsPreprocessorSyntaxHighlighting.OPERATOR,
                holder
            )
        }

        // Literal booleans: a bare true/false/yes/no IDENT not used as a function name.
        tokens.forEachIndexed { i, token ->
            if (token.type == IsPreprocessorExprTokenType.IDENT &&
                token.text.lowercase() in IS_PREPROCESSOR_BOOLEAN_WORDS &&
                tokens.getOrNull(i + 1)?.type != IsPreprocessorExprTokenType.LPAREN
            ) {
                val range = TextRange(base + token.start, base + token.end)
                highlight(range, IsPreprocessorAnnotatorHighlighting.BOOLEAN_LITERAL, holder)
                holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    "ISPP has no boolean literals; '${token.text}' is treated as an undefined identifier (0)"
                ).range(range).textAttributes(IsPreprocessorAnnotatorHighlighting.BOOLEAN_LITERAL).create()
            }
        }

        val parseResult = IsPreprocessorExprParser.parse(tokens, exprText.length)
        val resolver = buildTypeResolver(hostFile)
        val inference = resolver.inferenceAt(currentDirectiveOrder(directive, hostFile))
        inference.infer(parseResult.ast)

        (parseResult.errors + inference.errors).forEach { error ->
            holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                .range(TextRange(base + error.span.start, base + error.span.end))
                .create()
        }

        // Identifiers that refer to a non-existent #define are unresolved references (error, like #define).
        annotateUnresolvedReferences(directive, holder)
    }

    /**
     * Validates a subroutine directive (`#sub`/`#endsub`): highlights the `#sub` name like a declaration and
     * checks block structure (every `#sub` must be closed by `#endsub`; no stray `#endsub`) — mirroring
     * [annotateConditional].
     */
    private fun annotateSubroutine(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        if (ex.isSub()) {
            val nameId = ex.nameIdentifier
            val rawName = directive.value?.text?.trim().orEmpty()
            when {
                rawName.isEmpty() ->
                    holder.newAnnotation(HighlightSeverity.ERROR, "#sub requires a name")
                        .range(directive.textRange).create()

                // A #sub name must be a plain identifier: letters/digits/underscore, not starting with a digit
                // and with no trailing characters (e.g. `#sub My-Name`, `#sub 1x`, `#sub a b` are invalid). A
                // digit-leading name is lexed as a single VALUE_CHAR token (no IDENTIFIER), so this catches it.
                !SUBROUTINE_NAME.matches(rawName) ->
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Invalid #sub name '$rawName': only letters, digits and underscore are allowed " +
                                "(and the name must not start with a digit)"
                    )
                        .range(directive.value?.textRange ?: directive.textRange)
                        .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()

                nameId != null -> highlight(nameId.textRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder)
            }
        }

        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile() ?: return
        val problem = IsPreprocessorSubroutineStructure.structureOf(hostFile).problems[directive] ?: return
        val keywordNode = directive.identifier
        val message = when (problem) {
            IsSubroutineProblem.UnterminatedSub -> "Unterminated #sub: missing #endsub"
            IsSubroutineProblem.StrayEndsub -> "#endsub without matching #sub"
        }
        val hash = directive.node.findChildByType(IsPreprocessorTypes.HASH)
        val range = if (keywordNode != null)
            TextRange(hash?.startOffset ?: keywordNode.textRange.startOffset, keywordNode.textRange.endOffset)
        else directive.textRange
        holder.newAnnotation(HighlightSeverity.ERROR, message).range(range).create()
    }

    /**
     * Validates a `#for {Init; Cond; Incr} Body`: requires the brace group and a loop variable declared in the
     * initializer, type-checks the condition (must be integer-compatible), validates the init/increment
     * statements and the single same-line body expression, and reports unresolved references. The loop
     * variable is in scope only within these slots (its type is the type of the initializer's value).
     */
    private fun annotateFor(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val base = directive.textRange.startOffset

        val initText = ex.getForInitText()
        val initOffset = ex.getForInitOffsetInDirective()
        val condText = ex.getForConditionText()
        val condOffset = ex.getForConditionOffsetInDirective()
        val incrText = ex.getForIncrementText()
        val incrOffset = ex.getForIncrementOffsetInDirective()
        val bodyText = ex.getForBodyText()
        val bodyOffset = ex.getForBodyOffsetInDirective()

        if (initOffset < 0 || condOffset < 0 || incrOffset < 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#for requires '{Init; Condition; Increment} Body'")
                .range(directive.textRange).create()
            return
        }

        // ── loop variable ──
        val varName = ex.getForVariableName()
        if (varName == null) {
            val initBase = base + initOffset
            val range = if (!initText.isNullOrEmpty()) TextRange(initBase, initBase + initText.length)
            else directive.textRange
            holder.newAnnotation(HighlightSeverity.ERROR, "#for initializer must define a loop variable (e.g. 'i = 0')")
                .range(range).create()
        } else {
            ex.getForVariableNameNode()
                ?.let { highlight(it.textRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder) }
            // Paint every *use* of the loop variable (in the condition/increment/body) like the declaration —
            // italic define-name colour — so it is visually recognizable as the loop variable throughout.
            directive.references.filterIsInstance<IsPreprocessorForVariableReference>().forEach { ref ->
                highlight(
                    ref.rangeInElement.shiftRight(directive.textRange.startOffset),
                    IsPreprocessorAnnotatorHighlighting.DEFINE_NAME,
                    holder,
                )
            }
        }
        val varType = forInitValueType(directive, ex, hostFile)
        val extra = if (varName != null) mapOf(varName to varType) else emptyMap()

        // ── init / increment statements ──
        validateForStatement(directive, initText, initOffset, hostFile, holder, extra)
        validateForStatement(directive, incrText, incrOffset, hostFile, holder, extra)

        // ── condition: must be an integer-compatible expression ──
        if (condText.isNullOrBlank()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#for requires a condition").range(directive.textRange)
                .create()
        } else {
            val type = validateExpr(directive, condText, condOffset, hostFile, holder, extra)
            if (!type.intCompatible) {
                val condBase = base + condOffset
                holder.newAnnotation(HighlightSeverity.ERROR, "#for condition must be an integer expression")
                    .range(TextRange(condBase, condBase + condText.length)).create()
            }
        }

        // ── body: exactly one expression on the same line ──
        if (bodyText.isNullOrBlank()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#for requires a body expression on the same line")
                .range(directive.textRange).create()
        } else {
            validateExpr(directive, bodyText, bodyOffset, hostFile, holder, extra)
        }

        annotateUnresolvedReferences(directive, holder)
    }

    /**
     * Validates one `#for` init/increment slot, which may be an assignment (`Name = Expr`, possibly chained),
     * an increment/decrement (`i++`/`i--`/`++i`/`--i`) or a plain side-effect expression. Assignment targets
     * and the `++`/`--` operand are not full expressions, so only the value expression is type-checked.
     */
    private fun validateForStatement(
        directive: IsPreprocessorDirective,
        text: String?,
        offset: Int,
        hostFile: PsiFile?,
        holder: IsAnnotationSink,
        extraVariables: Map<String, IsPreprocessorExprType>,
    ) {
        if (text == null || offset < 0) return
        if (text.isBlank()) return
        if (FOR_INCREMENT.matches(text.trim())) return
        val eq = topLevelAssignmentIndex(text)
        if (eq != null) {
            val rhsStart = eq + 1
            validateForStatement(
                directive,
                text.substring(rhsStart),
                offset + rhsStart,
                hostFile,
                holder,
                extraVariables
            )
            return
        }
        validateExpr(directive, text, offset, hostFile, holder, extraVariables)
    }

    /** The type of a `#for` initializer's value (the final right-hand side after stripping assignments). */
    private fun forInitValueType(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        hostFile: PsiFile?,
    ): IsPreprocessorExprType {
        var text = ex.getForInitText() ?: return IsPreprocessorExprType.INT
        var eq = topLevelAssignmentIndex(text)
        while (eq != null) {
            text = text.substring(eq + 1)
            eq = topLevelAssignmentIndex(text)
        }
        val rhs = text.trim()
        if (rhs.isEmpty() || FOR_INCREMENT.matches(rhs)) return IsPreprocessorExprType.INT
        val order = currentDirectiveOrder(directive, hostFile)
        return buildTypeResolver(hostFile).inferenceAt(order).infer(IsPreprocessorExprParser.parse(rhs).ast)
    }

    /**
     * Index of the first top-level assignment `=` in [text] (not part of `==`/`<=`/`>=`/`!=`), ignoring
     * `()`/`[]`/`{}` nesting and string literals, or `null` when there is no plain assignment.
     */
    private fun topLevelAssignmentIndex(text: String): Int? {
        var depth = 0
        var quote: Char? = null
        for (i in text.indices) {
            val c = text[i]
            when {
                quote != null -> if (c == quote) quote = null
                c == '"' || c == '\'' -> quote = c
                c == '(' || c == '[' || c == '{' -> depth++
                c == ')' || c == ']' || c == '}' -> depth--
                c == '=' && depth == 0 -> {
                    val prev = text.getOrNull(i - 1)
                    val next = text.getOrNull(i + 1)
                    if (prev != '=' && prev != '<' && prev != '>' && prev != '!' && next != '=') return i
                }
            }
        }
        return null
    }

    /**
     * Validates an `#include`: the value must end up as a string (a literal or an ISPP expression of type
     * `str`), and a literal path must point to an existing file. The included file's *content* is not
     * checked — `#include` pastes raw text that may be a free-form fragment.
     */
    private fun annotateInclude(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val value = directive.value
        if (value == null || value.text.isBlank()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#include requires a string value")
                .range(directive.textRange).create()
            return
        }

        val exprText = value.text
        val base = value.textRange.startOffset
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()

        val tokens = IsPreprocessorExprTokenizer.tokenize(exprText)
        val parseResult = IsPreprocessorExprParser.parse(tokens, exprText.length)
        val resolver = hostFile?.preprocessorTypeResolver() ?: IsPreprocessorExprTypeResolver(emptyList())
        val inference = resolver.inferenceAt(currentDirectiveOrder(directive, hostFile))
        val type = inference.infer(parseResult.ast)

        (parseResult.errors + inference.errors).forEach { error ->
            holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                .range(TextRange(base + error.span.start, base + error.span.end))
                .create()
        }

        if (!type.strCompatible) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#include requires a string value")
                .range(value.textRange).create()
            return
        }

        // Existence/content checks only for a literal path (an expression's value cannot be resolved here).
        val string = ex.getIncludeLiteralString() ?: return
        val path = ex.getIncludePath() ?: return
        // The path inside the quotes; for an empty string fall back to the quotes themselves so the marker shows.
        val pathRange =
            if (path.isEmpty()) string.textRange
            else TextRange(string.textRange.startOffset + 1, string.textRange.endOffset - 1)

        if (path.isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#include path must not be empty")
                .range(pathRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }

        val baseDir = hostFile?.virtualFile?.parent
        val target = baseDir?.let { IsIncludePaths.resolve(it, path) }
        if (target == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Included file not found: '$path'")
                .range(pathRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }

        // A literal include whose target is trivially small is unnecessary: an empty file does nothing, a
        // single-line file can simply be inlined. Surface a weak warning with a matching quick fix. This is a
        // base check on the literal include itself, so it runs before the effective-script analysis below.
        if (!target.isDirectory) {
            val content = VfsUtilCore.loadText(target)
            when (nonTrailingLineCount(content)) {
                0 -> holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "#include points to an empty file")
                    .range(pathRange)
                    .withFix(RemoveIncludeQuickFix(directive))
                    .create()

                1 -> holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    "#include points to a single-line file; inline the line instead"
                )
                    .range(pathRange)
                    .withFix(ReplaceIncludeWithLineQuickFix(path, content.removeSuffix("\n").removeSuffix("\r")))
                    .create()
            }
        }

        // Re-entrancy guard: when the recording run replays this annotator over the in-memory effective script,
        // its remaining (unresolvable) #include lines must not trigger the effective analysis again. The base
        // checks above already ran; stop before the combined analysis below.
        if (hostFile.getUserData(EFFECTIVE_SCRIPT_MARKER) == true) return

        // Surface the problems that the inclusion introduces into the *combined* effective script (a flag
        // conflict with the main file, a fragment that is only valid in context, a transitively missing
        // include, …) on this #include. Problems of the included file in isolation are intentionally ignored.
        (hostFile as? IsPreprocessorHost)?.includeProblems(directive)?.forEach { problem ->
            holder.newAnnotation(problem.severity, problem.message)
                .range(pathRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    /**
     * Validates a `#pragma`: the sub-command must be one declared by the ISPP spec, and its argument must
     * match the declared kind — flag list (`option`/`parseroption`), a string expression
     * (`message`/`warning`/`error`/`include`/`inlinestart`/`inlineend`/`spansymbol`) or an integer
     * expression (`verboselevel`). Identifiers inside an expression argument resolve against the #defines of
     * the host file (see [annotateUnresolvedReferences]).
     */
    private fun annotatePragma(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val value = directive.value
        val subName = ex.getPragmaSubCommand()
        if (subName == null || value == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#pragma requires a sub-command")
                .range(directive.textRange).create()
            return
        }
        val subNode = value.node.findChildByType(IsPreprocessorTypes.IDENTIFIER) ?: return

        val spec = service<IsPreprocessorService>().spec.pragmaSubCommands
            .firstOrNull { it.name.equals(subName, ignoreCase = true) }
        if (spec == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown #pragma sub-command: '$subName'")
                .range(subNode.textRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }
        highlight(subNode.textRange, IsPreprocessorAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

        val argText = ex.getPragmaArgumentText()
        val argOffset = ex.getPragmaArgumentOffsetInDirective()
        val argRange = if (argText != null && argOffset >= 0) {
            val base = directive.textRange.startOffset + argOffset
            TextRange(base, base + argText.length)
        } else null

        when (spec.argument) {
            IsPreprocessorPragmaArgument.NONE ->
                if (argText != null && argRange != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "#pragma ${spec.name} does not take an argument")
                        .range(argRange).create()
                }

            IsPreprocessorPragmaArgument.FLAGS -> {
                if (argText == null || argOffset < 0) {
                    pragmaMissingArgument(directive, spec, holder)
                    return
                }
                annotatePragmaFlags(directive, spec, argText, argOffset, holder)
            }

            IsPreprocessorPragmaArgument.STR, IsPreprocessorPragmaArgument.INT -> {
                if (argText == null || argOffset < 0) {
                    pragmaMissingArgument(directive, spec, holder)
                    return
                }
                annotatePragmaExpression(directive, spec, argText, argOffset, argRange, holder)
            }
        }
    }

    private fun pragmaMissingArgument(
        directive: IsPreprocessorDirective,
        spec: IsPreprocessorPragmaSpec,
        holder: IsAnnotationSink,
    ) {
        holder.newAnnotation(HighlightSeverity.ERROR, "#pragma ${spec.name} requires an argument")
            .range(directive.textRange).create()
    }

    /** Validates the `-<letter>(+|-)` flags of an `option`/`parseroption` pragma against the spec letters. */
    private fun annotatePragmaFlags(
        directive: IsPreprocessorDirective,
        spec: IsPreprocessorPragmaSpec,
        argText: String,
        argOffset: Int,
        holder: IsAnnotationSink,
    ) {
        val base = directive.textRange.startOffset + argOffset
        val allowed = spec.flagLetters.map { it.letter.lowercase() }.toSet()
        NON_WHITESPACE.findAll(argText).forEach { match ->
            val token = match.value
            val range = TextRange(base + match.range.first, base + match.range.last + 1)
            val flag = PRAGMA_FLAG.matchEntire(token)
            if (flag == null) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Invalid #pragma ${spec.name} flag '$token'; expected -<letter>(+|-)"
                ).range(range).create()
                return@forEach
            }
            val letter = flag.groupValues[1]
            if (letter.lowercase() !in allowed) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Unknown #pragma ${spec.name} flag letter '$letter'"
                ).range(range).textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE).create()
            }
        }
    }

    /** Validates the string/integer expression argument of a `#pragma`, including type and references. */
    private fun annotatePragmaExpression(
        directive: IsPreprocessorDirective,
        spec: IsPreprocessorPragmaSpec,
        argText: String,
        argOffset: Int,
        argRange: TextRange?,
        holder: IsAnnotationSink,
    ) {
        val base = directive.textRange.startOffset + argOffset

        val tokens = IsPreprocessorExprTokenizer.tokenize(argText)
        tokens.filter { it.type in OPERATOR_TOKEN_TYPES }.forEach { token ->
            highlight(
                TextRange(base + token.start, base + token.end),
                IsPreprocessorSyntaxHighlighting.OPERATOR,
                holder,
            )
        }

        val parseResult = IsPreprocessorExprParser.parse(tokens, argText.length)
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val resolver = buildTypeResolver(hostFile)
        val inference = resolver.inferenceAt(currentDirectiveOrder(directive, hostFile))
        val type = inference.infer(parseResult.ast)

        (parseResult.errors + inference.errors).forEach { error ->
            holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                .range(TextRange(base + error.span.start, base + error.span.end))
                .create()
        }

        // Identifiers in the argument that refer to a non-existent #define are unresolved references.
        annotateUnresolvedReferences(directive, holder)

        when (spec.argument) {
            IsPreprocessorPragmaArgument.STR ->
                if (!type.strCompatible && argRange != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "#pragma ${spec.name} requires a string value")
                        .range(argRange).create()
                }

            IsPreprocessorPragmaArgument.INT -> {
                if (!type.intCompatible && argRange != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "#pragma ${spec.name} requires an integer value")
                        .range(argRange).create()
                }
                // verboselevel accepts only 0..10; flag a literal out of range.
                if (spec.name.equals("verboselevel", ignoreCase = true) && argRange != null) {
                    argText.trim().toIntOrNull()?.let { level ->
                        if (level !in 0..10) {
                            holder.newAnnotation(
                                HighlightSeverity.ERROR,
                                "#pragma verboselevel must be between 0 and 10"
                            ).range(argRange).create()
                        }
                    }
                }
            }

            else -> {}
        }
    }

    /**
     * Range of a #define name that illegally starts with a digit, or `null` when the name is valid.
     *
     * A valid name starts with a letter/underscore and is lexed as a single IDENTIFIER. A name beginning
     * with a digit is instead lexed as a NUMBER or VALUE_CHAR token, so the first value token starting with
     * a digit marks an invalid name.
     */
    private fun digitLeadingNameRange(directive: IsPreprocessorDirective): TextRange? {
        val valueNode = directive.value?.node ?: return null
        val first = valueNode.getChildren(null)
            .firstOrNull { it.elementType != TokenType.WHITE_SPACE && it.textLength > 0 } ?: return null
        if (first.text.firstOrNull()?.isDigit() != true) return null
        return first.textRange
    }

    private fun annotateUnresolvedReferences(directive: IsPreprocessorDirective, holder: IsAnnotationSink) {
        val refs = directive.references.filterIsInstance<IsPreprocessorExpressionReference>()
        if (refs.isEmpty()) return

        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        // #define/#dim names are referenceable everywhere; #sub names are *not* — a subroutine may only be
        // invoked as the body of a #for loop, so its name is tracked separately and validated below.
        val definedNames = hostFile?.isppDirectives
            ?.mapNotNull { d ->
                val dex = d as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
                // An array element `#define Name[i]` is a usage, not a declaration; the `#dim` declares the name.
                if (dex.isArrayElementDefine()) null
                else dex.getArrayName() ?: dex.getDefineName()
            }
            ?.toSet() ?: emptySet()
        val subNames = hostFile?.isppDirectives
            ?.mapNotNull { (it as? IsPreprocessorDirectiveEx)?.getSubroutineName() }
            ?.toSet() ?: emptySet()
        val spec = service<IsPreprocessorService>().spec

        // The directive-relative range of the #for body slot (where a #sub call is legitimate), or null.
        val ex = directive as? IsPreprocessorDirectiveEx
        val forBodyRange = if (ex?.isFor() == true) {
            val bodyText = ex.getForBodyText()
            val bodyOffset = ex.getForBodyOffsetInDirective()
            if (!bodyText.isNullOrEmpty() && bodyOffset >= 0) bodyOffset until (bodyOffset + bodyText.length)
            else null
        } else null

        refs.forEach { ref ->
            val name = ref.canonicalText
            val range = ref.rangeInElement.shiftRight(directive.textRange.startOffset)
            if (definedNames.any { it.equals(name, ignoreCase = true) }) return@forEach
            val knownBuiltin = spec.builtinFunctions.any { it.name.equals(name, ignoreCase = true) } ||
                    spec.predefinedVariables.any { it.name.equals(name, ignoreCase = true) }
            if (knownBuiltin) return@forEach

            // A symbol handed to ISCC from outside (`/D<name>`) is defined for the build although nothing in
            // the script declares it — reporting it as unresolved would flag a valid script.
            if (hostFile?.isExternalPreprocessorSymbol(name) == true) return@forEach

            // A #sub name: valid only when it is the #for body; anywhere else it is a misuse, not a value.
            if (subNames.any { it.equals(name, ignoreCase = true) }) {
                if (forBodyRange != null && ref.rangeInElement.startOffset in forBodyRange) return@forEach
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'$name' is a #sub and can only be called as the body of a #for loop"
                )
                    .range(range)
                    .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()
                return@forEach
            }

            // An identifier immediately followed by '(' is a call, so report it as an unknown function
            // rather than as an unknown value — the name is neither a built-in nor a macro of this file.
            val message = if (isCallSite(directive, ref.rangeInElement.endOffset)) {
                "Unknown preprocessor function: '$name'"
            } else {
                "Unresolved preprocessor reference: '$name'"
            }
            holder.newAnnotation(HighlightSeverity.ERROR, message)
                .range(range)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    /** Whether the identifier ending at [endOffsetInDirective] is directly followed by an argument list. */
    private fun isCallSite(directive: IsPreprocessorDirective, endOffsetInDirective: Int): Boolean {
        val text = directive.text
        var i = endOffsetInDirective
        while (i < text.length && text[i].isWhitespace()) i++
        return i < text.length && text[i] == '('
    }

    /**
     * Analyses the `#define` expression: highlights operators and reports syntax errors (missing operator,
     * unbalanced parenthesis, …) and type errors (e.g. `"a" * "b"`) at the precise offending token. Reference
     * types are resolved recursively through the names of the other #defines in the host file.
     */
    private fun annotateExpression(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val exprText = ex.getDefineExpressionText() ?: return
        val exprOffset = ex.getDefineExpressionOffsetInDirective()
        if (exprOffset < 0) return
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        // Inside a function-like macro body its own parameters are in scope, with the type the declaration
        // states (`#define M(int A) …`) resp. the one probed from the body — so passing a `str` parameter
        // into an `int` parameter of another macro is caught right here.
        val parameters = if (ex.isFunctionMacro()) macroParameterTypes(ex, hostFile) else emptyMap()
        // Shared expression validation: operators, syntax/type errors, array-read bounds.
        validateExpr(directive, exprText, exprOffset, hostFile, holder, parameters)
    }

    /** The in-scope types of a function-like macro's own parameters, declared type first. */
    private fun macroParameterTypes(
        ex: IsPreprocessorDirectiveEx,
        hostFile: PsiFile?,
    ): Map<String, IsPreprocessorExprType> {
        val declarations = ex.getMacroParameterDeclarations()
        if (declarations.isEmpty()) return emptyMap()
        val name = ex.getDefineName()
        val probed = if (name.isNullOrEmpty()) emptyList()
        else hostFile?.preprocessorTypeResolver()?.probableMacroParameterTypes(name) ?: emptyList()
        return declarations.mapIndexed { index, parameter ->
            parameter.name to (parameter.declaredType ?: probed.getOrNull(index) ?: IsPreprocessorExprType.ANY)
        }.toMap()
    }

    /**
     * Validates a `#dim`/`#redim` array declaration: highlights the optional scope keyword and the array name,
     * rejects a reserved/digit-leading name, requires and type-checks the `\[Size]` expression (integer, positive
     * when static), requires a preceding `#dim` for a `#redim`, and validates an optional inline initialiser
     * (each element expression plus a count-vs-size check).
     */
    private fun annotateArrayDeclaration(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        highlightVisibility(ex, holder)
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val order = currentDirectiveOrder(directive, hostFile)
        val keyword = directive.identifier?.text ?: if (ex.isDim()) "dim" else "redim"

        digitLeadingNameRange(directive)?.let { range ->
            holder.newAnnotation(HighlightSeverity.ERROR, "An array name must not start with a digit")
                .range(range).create()
            return
        }

        val nameId = ex.nameIdentifier
        if (nameId != null) {
            val forbidden = service<IsPreprocessorService>().spec.forbiddenVariableNames
                .firstOrNull { it.name.equals(nameId.text, ignoreCase = true) }
            if (forbidden != null) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'${nameId.text}' is a reserved preprocessor keyword and cannot be used as an array name"
                ).range(nameId.textRange).create()
                return
            }
            highlight(nameId.textRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder)
        }

        // #redim requires an existing array of that name declared earlier.
        val name = ex.getArrayName()
        if (ex.isRedim() && name != null && hostFile?.precedingArray(name, order) == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#redim '$name' has no matching #dim")
                .range(nameId?.textRange ?: directive.textRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }

        val sizeText = ex.getArraySizeText()
        val sizeOffset = ex.getArraySizeOffsetInDirective()
        if (sizeText == null || sizeText.isBlank() || sizeOffset < 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "#$keyword requires a size in '[…]'")
                .range(directive.textRange).create()
            return
        }
        val sizeBase = directive.textRange.startOffset + sizeOffset
        val sizeRange = TextRange(sizeBase, sizeBase + sizeText.length)
        val sizeType = validateExpr(directive, sizeText, sizeOffset, hostFile, holder)
        if (!sizeType.intCompatible) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Array size must be an integer").range(sizeRange).create()
        }
        sizeText.trim().toLongOrNull()?.let { staticSize ->
            if (staticSize <= 0) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Array size must be positive").range(sizeRange).create()
            }
        }

        // Optional inline initialiser `{v0, v1, …}`: validate each element and the count against a static size.
        val initText = ex.getArrayInitializerText()
        val initOffset = ex.getArrayInitializerOffsetInDirective()
        if (initText != null && initOffset >= 0) {
            val elements = splitInitializerElements(initText)
            var elementStart = 0
            elements.forEach { element ->
                val trimmedLeading = element.length - element.trimStart().length
                val trimmed = element.trim()
                if (trimmed.isNotEmpty()) {
                    validateExpr(directive, trimmed, initOffset + elementStart + trimmedLeading, hostFile, holder)
                }
                elementStart += element.length + 1 // +1 for the consumed comma
            }
            sizeText.trim().toLongOrNull()?.let { staticSize ->
                if (elements.size.toLong() != staticSize) {
                    val initBase = directive.textRange.startOffset + initOffset
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "${elements.size} initializer${if (elements.size == 1) "" else "s"} given, " +
                                "but array '$name' has $staticSize element${if (staticSize == 1L) "" else "s"}"
                    ).range(TextRange(initBase, initBase + initText.length)).create()
                }
            }
        }

        annotateUnresolvedReferences(directive, holder)
    }

    /**
     * Validates an array element assignment `#define Name\[Index] Value`: the name must resolve to a `#dim`, the
     * index must be an integer (and in bounds when both index and size are static constants), and the value is
     * type-checked like any `#define` expression.
     */
    private fun annotateArrayElementDefine(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: IsAnnotationSink,
    ) {
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val order = currentDirectiveOrder(directive, hostFile)
        val name = ex.getDefineArrayName() ?: return
        val base = directive.textRange.startOffset
        val idxOffset = ex.getDefineArrayIndexOffsetInDirective()
        val nameRange =
            if (idxOffset >= 0) TextRange(base + idxOffset - 1 - name.length, base + idxOffset - 1)
            else directive.textRange

        if (hostFile?.precedingArray(name, order) == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "'$name' is not declared as an array")
                .range(nameRange)
                .textAttributes(IsPreprocessorAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }
        highlight(nameRange, IsPreprocessorAnnotatorHighlighting.DEFINE_NAME, holder)

        val indexText = ex.getDefineArrayIndexText()
        if (indexText.isNullOrBlank() || idxOffset < 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "An array index is required in '[…]'")
                .range(nameRange).create()
        } else {
            val indexBase = base + idxOffset
            val indexRange = TextRange(indexBase, indexBase + indexText.length)
            val indexType = validateExpr(directive, indexText, idxOffset, hostFile, holder)
            if (!indexType.intCompatible) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Array index must be an integer")
                    .range(indexRange).create()
            }
            val staticIndex = indexText.trim().toLongOrNull()
            val size = hostFile.arraySize(name, order)
            if (staticIndex != null && size != null && (staticIndex < 0 || staticIndex >= size)) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Index $staticIndex out of bounds for array '$name' of size $size"
                ).range(indexRange).create()
            }
        }

        // An array element assignment requires a value — unlike a plain `#define X` which may be valueless.
        val valueText = ex.getDefineExpressionText()
        val valueOffset = ex.getDefineExpressionOffsetInDirective()
        if (valueText == null || valueOffset < 0) {
            holder.newAnnotation(HighlightSeverity.ERROR, "An array element assignment requires a value")
                .range(directive.textRange).create()
        } else {
            validateExpr(directive, valueText, valueOffset, hostFile, holder)
        }

        annotateUnresolvedReferences(directive, holder)
    }

    /**
     * Splits an inline-initialiser body on top-level commas (ignoring commas nested in brackets/parens/braces or
     * inside strings), preserving each raw element text so per-element offsets can be reconstructed.
     */
    private fun splitInitializerElements(text: String): List<String> {
        val parts = mutableListOf<String>()
        val sb = StringBuilder()
        var depth = 0
        var inString = false
        for (c in text) {
            when {
                inString -> {
                    sb.append(c); if (c == '"') inString = false
                }

                c == '"' -> {
                    sb.append(c); inString = true
                }

                c == '(' || c == '[' || c == '{' -> {
                    depth++; sb.append(c)
                }

                c == ')' || c == ']' || c == '}' -> {
                    depth--; sb.append(c)
                }

                c == ',' && depth == 0 -> {
                    parts += sb.toString(); sb.clear()
                }

                else -> sb.append(c)
            }
        }
        parts += sb.toString()
        return parts
    }

    /**
     * Highlights the operators of [exprText] and reports its syntax and type errors at [offsetInDirective],
     * returning the inferred type. Shared by the array size/index/value and inline-initialiser checks.
     */
    private fun validateExpr(
        directive: IsPreprocessorDirective,
        exprText: String,
        offsetInDirective: Int,
        hostFile: PsiFile?,
        holder: IsAnnotationSink,
        extraVariables: Map<String, IsPreprocessorExprType> = emptyMap(),
    ): IsPreprocessorExprType {
        val base = directive.textRange.startOffset + offsetInDirective
        val tokens = IsPreprocessorExprTokenizer.tokenize(exprText)
        tokens.filter { it.type in OPERATOR_TOKEN_TYPES }.forEach { token ->
            highlight(
                TextRange(base + token.start, base + token.end),
                IsPreprocessorSyntaxHighlighting.OPERATOR,
                holder
            )
        }
        val parseResult = IsPreprocessorExprParser.parse(tokens, exprText.length)
        val order = currentDirectiveOrder(directive, hostFile)
        val resolver = buildTypeResolver(hostFile, extraVariables)
        val inference = resolver.inferenceAt(order)
        val type = inference.infer(parseResult.ast)
        (parseResult.errors + inference.errors).forEach { error ->
            holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                .range(TextRange(base + error.span.start, base + error.span.end))
                .create()
        }
        // Static out-of-bounds reads (`arr[9]` on an array of size 3) — only when index and size are constant.
        forEachIndexNode(parseResult.ast) { node ->
            val idxText = exprText.substring(node.index.span.start, node.index.span.end).trim()
            val idx = idxText.toLongOrNull() ?: return@forEachIndexNode
            val size = hostFile?.arraySize(node.name, order) ?: return@forEachIndexNode
            if (idx < 0 || idx >= size) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Index $idx out of bounds for array '${node.name}' of size $size"
                ).range(TextRange(base + node.index.span.start, base + node.index.span.end)).create()
            }
        }
        return type
    }

    /** Visits every array index-access node in the [node] tree, applying [action] to each. */
    private fun forEachIndexNode(
        node: IsPreprocessorExprNode,
        action: (IsPreprocessorExprIndex) -> Unit,
    ) {
        when (node) {
            is IsPreprocessorExprIndex -> {
                action(node); forEachIndexNode(node.index, action)
            }

            is IsPreprocessorExprParen -> forEachIndexNode(node.inner, action)
            is IsPreprocessorExprUnary -> forEachIndexNode(node.operand, action)
            is IsPreprocessorExprBinary -> {
                forEachIndexNode(node.left, action); forEachIndexNode(node.right, action)
            }

            is IsPreprocessorExprTernary -> {
                forEachIndexNode(node.condition, action)
                forEachIndexNode(node.whenTrue, action)
                forEachIndexNode(node.whenFalse, action)
            }

            is IsPreprocessorExprCall -> node.arguments.forEach { forEachIndexNode(it, action) }
            else -> {}
        }
    }

    /** Builds a resolver over the simple #defines and function-like macros of [hostFile] plus the ISPP spec. */
    private fun buildTypeResolver(
        hostFile: PsiFile?,
        extraVariables: Map<String, IsPreprocessorExprType> = emptyMap(),
    ): IsPreprocessorExprTypeResolver =
        hostFile?.preprocessorTypeResolver(extraVariables) ?: IsPreprocessorExprTypeResolver(emptyList())

    /** Host-file offset (declaration order) of [directive], or [Int.MAX_VALUE] when it cannot be located. */
    private fun currentDirectiveOrder(directive: IsPreprocessorDirective, hostFile: PsiFile?): Int =
        hostFile?.isppDirectivesWithHostOffset?.firstOrNull { it.first === directive }?.second ?: Int.MAX_VALUE

    private fun isDefineUsed(directive: IsPreprocessorDirective, name: String): Boolean {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile).asIsppHostFile() ?: return true

        // Check {#Name} references anywhere in the host file (script section constants); the host decides.
        if ((hostFile as? IsPreprocessorHost)?.isPreprocessorNameReferencedAsConstant(name) == true) return true

        // Check cross-references inside other #define expressions. A #undef references the #define but is
        // not a real use, so it must not suppress the "never used" warning.
        return hostFile.isppDirectives
            .filter { it !== directive }
            .filter { (it as? IsPreprocessorDirectiveEx)?.isUndef() != true }
            .any { other ->
                other.references.any { ref ->
                    // A macro parameter that happens to share the name is a local symbol, not a use.
                    ref !is IsPreprocessorMacroParameterReference &&
                            ref.canonicalText.equals(name, ignoreCase = true)
                }
            }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: IsAnnotationSink) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()

    /**
     * Number of content lines in [text], ignoring a single trailing line break. A blank/empty file yields 0,
     * a one-line file (with or without a trailing newline) yields 1.
     */
    private fun nonTrailingLineCount(text: String): Int {
        if (text.isBlank()) return 0
        return text.removeSuffix("\n").removeSuffix("\r").count { it == '\n' } + 1
    }
}
