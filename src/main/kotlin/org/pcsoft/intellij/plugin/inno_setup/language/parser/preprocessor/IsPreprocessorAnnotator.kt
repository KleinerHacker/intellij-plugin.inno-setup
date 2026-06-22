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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

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
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.EFFECTIVE_SCRIPT_MARKER
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsEffectiveScriptProblems
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsPreprocessorExpressionReference
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprParser
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprTokenType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprTokenizer
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprTypeResolver
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.RemoveIncludeQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.RemoveUnusedDefineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.RemoveUselessUndefQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.ReplaceIncludeWithLineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionConstant
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorPragmaArgument
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorPragmaSpec

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
            IsPreprocessorExprTokenType.COMMA,
            IsPreprocessorExprTokenType.QUESTION,
            IsPreprocessorExprTokenType.COLON,
        )

        /** A single `#pragma option`/`parseroption` flag, e.g. `-v+` or `-c-`. */
        val PRAGMA_FLAG = Regex("^-([A-Za-z])([+-])$")

        /** Splits a flag argument into its whitespace-separated tokens (with their positions). */
        val NON_WHITESPACE = Regex("\\S+")
    }

    /**
     * Annotates the supplied PSI element when it matches this component's checks.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        // Token-level syntax highlighting (strings/numbers). Inside the ISPP injection the injected
        // SyntaxHighlighter lexer does not paint reliably in the host editor, so the colours are applied
        // here through the annotator pass — the same path the directive keyword highlighting uses.
        when (element.node?.elementType) {
            IsPreprocessorTypes.QUOTE, IsPreprocessorTypes.STRING_PART ->
                highlight(element.textRange, IsPreprocessorSyntaxHighlighting.STRING, holder)

            IsPreprocessorTypes.NUMBER ->
                highlight(element.textRange, IsPreprocessorSyntaxHighlighting.NUMBER, holder)
        }

        if (element is IsPreprocessorDirective) annotateDirective(element, holder)
    }

    private fun annotateDirective(directive: IsPreprocessorDirective, holder: AnnotationHolder) {
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }

        highlight(keywordRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

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
        if (!ex.isDefine()) return

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
            highlight(nameIdentifier.textRange, IsSectionAnnotatorHighlighting.DEFINE_NAME, holder)
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                .withFix(RemoveUnusedDefineQuickFix(directive))
                .create()
        }
    }

    /** Highlights the optional scope/visibility keyword of a `#define`/`#undef` like a keyword. */
    private fun highlightVisibility(ex: IsPreprocessorDirectiveEx, holder: AnnotationHolder) {
        val visibility = ex.getVisibilityIdentifier() ?: return
        highlight(visibility.textRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
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
        holder: AnnotationHolder,
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
        val resolved = directive.references
            .filterIsInstance<IsPreprocessorExpressionReference>()
            .any { it.resolve() != null }

        if (resolved) {
            highlight(nameIdentifier.textRange, IsSectionAnnotatorHighlighting.DEFINE_NAME, holder)
        } else {
            holder.newAnnotation(
                HighlightSeverity.WEAK_WARNING,
                "#undef '${nameIdentifier.text}' has no matching #define"
            )
                .range(nameIdentifier.textRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                .withFix(RemoveUselessUndefQuickFix(directive))
                .create()
        }
    }

    /**
     * Validates an `#include`: the value must end up as a string (a literal or an ISPP expression of type
     * `str`), and a literal path must point to an existing file. The included file's *content* is not
     * checked — `#include` pastes raw text that may be a free-form fragment.
     */
    private fun annotateInclude(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: AnnotationHolder,
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }

        val baseDir = hostFile?.virtualFile?.parent
        val target = baseDir?.let { IsIncludePaths.resolve(it, path) }
        if (target == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Included file not found: '$path'")
                .range(pathRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
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
        if (hostFile !is IsScriptFile) return
        IsEffectiveScriptProblems.forHost(hostFile)[directive]?.forEach { problem ->
            holder.newAnnotation(problem.severity, problem.message)
                .range(pathRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
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
        holder: AnnotationHolder,
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
            return
        }
        highlight(subNode.textRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

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
        holder: AnnotationHolder,
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
        holder: AnnotationHolder,
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
                ).range(range).textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE).create()
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
        holder: AnnotationHolder,
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

    private fun annotateUnresolvedReferences(directive: IsPreprocessorDirective, holder: AnnotationHolder) {
        val refs = directive.references.filterIsInstance<IsPreprocessorExpressionReference>()
        if (refs.isEmpty()) return

        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val definedNames = hostFile?.isppDirectives
            ?.mapNotNull { (it as? IsPreprocessorDirectiveEx)?.getDefineName() }
            ?.toSet() ?: emptySet()
        val spec = service<IsPreprocessorService>().spec

        refs.forEach { ref ->
            val name = ref.canonicalText
            if (name in definedNames) return@forEach
            val knownBuiltin = spec.builtinFunctions.any { it.name.equals(name, ignoreCase = true) } ||
                    spec.predefinedVariables.any { it.name.equals(name, ignoreCase = true) }
            if (knownBuiltin) return@forEach

            val range = ref.rangeInElement.shiftRight(directive.textRange.startOffset)
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved preprocessor reference: '$name'")
                .range(range)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    /**
     * Analyses the `#define` expression: highlights operators and reports syntax errors (missing operator,
     * unbalanced parenthesis, …) and type errors (e.g. `"a" * "b"`) at the precise offending token. Reference
     * types are resolved recursively through the names of the other #defines in the host file.
     */
    private fun annotateExpression(
        directive: IsPreprocessorDirective,
        ex: IsPreprocessorDirectiveEx,
        holder: AnnotationHolder,
    ) {
        val exprText = ex.getDefineExpressionText() ?: return
        val exprOffset = ex.getDefineExpressionOffsetInDirective()
        if (exprOffset < 0) return
        val base = directive.textRange.startOffset + exprOffset

        val tokens = IsPreprocessorExprTokenizer.tokenize(exprText)
        tokens.filter { it.type in OPERATOR_TOKEN_TYPES }.forEach { token ->
            highlight(
                TextRange(base + token.start, base + token.end),
                IsPreprocessorSyntaxHighlighting.OPERATOR,
                holder,
            )
        }

        val parseResult = IsPreprocessorExprParser.parse(tokens, exprText.length)

        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile()
        val resolver = buildTypeResolver(hostFile)
        val inference = resolver.inferenceAt(currentDirectiveOrder(directive, hostFile))
        inference.infer(parseResult.ast)

        (parseResult.errors + inference.errors).forEach { error ->
            holder.newAnnotation(HighlightSeverity.ERROR, error.message)
                .range(TextRange(base + error.span.start, base + error.span.end))
                .create()
        }
    }

    /** Builds a resolver over the simple #defines and function-like macros of [hostFile] plus the ISPP spec. */
    private fun buildTypeResolver(hostFile: PsiFile?): IsPreprocessorExprTypeResolver =
        hostFile?.preprocessorTypeResolver() ?: IsPreprocessorExprTypeResolver(emptyList())

    /** Host-file offset (declaration order) of [directive], or [Int.MAX_VALUE] when it cannot be located. */
    private fun currentDirectiveOrder(directive: IsPreprocessorDirective, hostFile: PsiFile?): Int =
        hostFile?.isppDirectivesWithHostOffset?.firstOrNull { it.first === directive }?.second ?: Int.MAX_VALUE

    private fun isDefineUsed(directive: IsPreprocessorDirective, name: String): Boolean {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile).asIsppHostFile() ?: return true

        // Check {#Name} references anywhere in the ISS host file.
        val usedAsConstant = PsiTreeUtil.findChildrenOfType(hostFile, IsSectionConstant::class.java).any { constant ->
            val body = constant.constantBody.text.trim()
            body.startsWith("#") && body.trimStart('#').trim().equals(name, ignoreCase = true)
        }
        if (usedAsConstant) return true

        // Check cross-references inside other #define expressions. A #undef references the #define but is
        // not a real use, so it must not suppress the "never used" warning.
        return hostFile.isppDirectives
            .filter { it !== directive }
            .filter { (it as? IsPreprocessorDirectiveEx)?.isUndef() != true }
            .any { other -> other.references.any { ref -> ref.canonicalText.equals(name, ignoreCase = true) } }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
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
