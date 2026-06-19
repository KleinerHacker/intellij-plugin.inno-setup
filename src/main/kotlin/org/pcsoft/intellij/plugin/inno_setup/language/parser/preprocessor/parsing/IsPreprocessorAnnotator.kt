/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
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
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.quickfix.RemoveUnusedDefineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.preprocessorTypeResolver
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionConstant
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService

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
        if (!ex.isDefine()) return

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
            .getTopLevelFile(directive.containingFile) as? IsScriptFile

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

        // Re-entrancy guard: when the recording run replays this annotator over the in-memory effective script,
        // its remaining (unresolvable) #include lines must not trigger the effective analysis again. The base
        // checks above already ran; stop before the combined analysis below.
        if (hostFile.getUserData(EFFECTIVE_SCRIPT_MARKER) == true) return

        // Surface the problems that the inclusion introduces into the *combined* effective script (a flag
        // conflict with the main file, a fragment that is only valid in context, a transitively missing
        // include, …) on this #include. Problems of the included file in isolation are intentionally ignored.
        IsEffectiveScriptProblems.forHost(hostFile)[directive]?.forEach { problem ->
            holder.newAnnotation(problem.severity, problem.message)
                .range(pathRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
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
            .getTopLevelFile(directive.containingFile) as? IsScriptFile
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
            .getTopLevelFile(directive.containingFile) as? IsScriptFile
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
    private fun buildTypeResolver(hostFile: IsScriptFile?): IsPreprocessorExprTypeResolver =
        hostFile?.preprocessorTypeResolver() ?: IsPreprocessorExprTypeResolver(emptyList())

    /** Host-file offset (declaration order) of [directive], or [Int.MAX_VALUE] when it cannot be located. */
    private fun currentDirectiveOrder(directive: IsPreprocessorDirective, hostFile: IsScriptFile?): Int =
        hostFile?.isppDirectivesWithHostOffset?.firstOrNull { it.first === directive }?.second ?: Int.MAX_VALUE

    private fun isDefineUsed(directive: IsPreprocessorDirective, name: String): Boolean {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile) as? IsScriptFile ?: return true

        // Check {#Name} references anywhere in the ISS host file.
        val usedAsConstant = PsiTreeUtil.findChildrenOfType(hostFile, IsSectionConstant::class.java).any { constant ->
            val body = constant.constantBody.text.trim()
            body.startsWith("#") && body.trimStart('#').trim().equals(name, ignoreCase = true)
        }
        if (usedAsConstant) return true

        // Check cross-references inside other #define expressions.
        return hostFile.isppDirectives
            .filter { it !== directive }
            .any { other -> other.references.any { ref -> ref.canonicalText.equals(name, ignoreCase = true) } }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
