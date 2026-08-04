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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsIncludeFileReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorExpressionReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorForVariableReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IS_PREPROCESSOR_BOOLEAN_WORDS
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorBuiltinParameterKind
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprTokenType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprTokenizer
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorQuotedString
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorPragmaArgument

abstract class IsPreprocessorDirectiveMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IsPreprocessorDirectiveEx {

    private fun valueIdentifiers(): Array<ASTNode> =
        (this as IsPreprocessorDirective).value
            ?.node?.getChildren(TokenSet.create(IsPreprocessorTypes.IDENTIFIER))
            ?: emptyArray()

    /** The ISPP scope/visibility keywords (`public`/`protected`/`private`), lower-cased. */
    private fun visibilityKeywords(): Set<String> =
        service<IsPreprocessorService>().spec.visibilityKeywords.map { it.name.lowercase() }.toSet()

    /**
     * The leading value identifier that acts as a scope/visibility keyword, or `null`.
     *
     * Only `#define`/`#undef` may carry a scope, and the keyword is recognized **only when a name
     * follows it** (a second identifier). This keeps `#define public 1` interpreting `public` as the
     * (forbidden) name, while `#define public Foo 1` treats `public` as the scope and `Foo` as the name.
     */
    private fun visibilityNode(): ASTNode? {
        if (!isDefine() && !isUndef() && !isArrayDeclaration()) return null
        val ids = valueIdentifiers()
        val first = ids.firstOrNull() ?: return null
        if (first.text.lowercase() in visibilityKeywords() && ids.size >= 2) return first
        return null
    }

    private fun nameNode(): ASTNode? {
        val ids = valueIdentifiers()
        return if (visibilityNode() != null) ids.getOrNull(1) else ids.firstOrNull()
    }

    /** Raw text of the value node that follows the name identifier (no trimming, quotes kept). */
    private fun rawAfterName(): String? {
        val value = (this as IsPreprocessorDirective).value ?: return null
        val name = nameNode() ?: return null
        val start = name.startOffset - value.textRange.startOffset + name.textLength
        return value.text.substring(start)
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isDefine(): Boolean =
        (this as IsPreprocessorDirective).identifier?.text?.equals("define", ignoreCase = true) == true

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isUndef(): Boolean =
        (this as IsPreprocessorDirective).identifier?.text?.equals("undef", ignoreCase = true) == true

    private fun keywordEquals(vararg keywords: String): Boolean {
        val kw = (this as IsPreprocessorDirective).identifier?.text ?: return false
        return keywords.any { kw.equals(it, ignoreCase = true) }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIf(): Boolean = keywordEquals("if")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isElif(): Boolean = keywordEquals("elif")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isElse(): Boolean = keywordEquals("else")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isEndif(): Boolean = keywordEquals("endif")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfdef(): Boolean = keywordEquals("ifdef")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfndef(): Boolean = keywordEquals("ifndef")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfdefFamily(): Boolean = keywordEquals("ifdef", "ifndef")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfExist(): Boolean = keywordEquals("ifexist")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfNexist(): Boolean = keywordEquals("ifnexist")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfExistFamily(): Boolean = keywordEquals("ifexist", "ifnexist")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isIfElif(): Boolean = isIf() || isElif()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isConditionalOpener(): Boolean = keywordEquals("if", "ifdef", "ifndef", "ifexist", "ifnexist")

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isConditionalDirective(): Boolean = isConditionalOpener() || isElif() || isElse() || isEndif()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getConditionExpressionText(): String? = conditionExpression()?.first

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getConditionExpressionOffsetInDirective(): Int = conditionExpression()?.second ?: -1

    /**
     * The condition of an `#if`/`#elif` (the whole value text, trimmed) together with its start offset
     * inside the directive's text. `null` when this is not an `#if`/`#elif` or there is no condition.
     */
    private fun conditionExpression(): Pair<String, Int>? {
        if (!isIfElif()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val raw = value.text
        val leadingWs = raw.length - raw.trimStart().length
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return trimmed to (valueOffsetInDirective + leadingWs)
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getVisibilityIdentifier(): PsiElement? = visibilityNode()?.psi

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getDefineName(): String? {
        if (!isDefine()) return null
        return nameNode()?.text
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isFunctionMacro(): Boolean {
        if (!isDefine()) return false
        // The parameter list opens immediately after the name, with no whitespace in between.
        return rawAfterName()?.startsWith("(") == true
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getDefineValue(): String? {
        if (!isDefine()) return null
        val after = rawAfterName() ?: return null
        if (after.startsWith("(")) return null  // function-like macro: no simple constant value
        if (after.startsWith("[")) return null  // array element assignment: not a scalar constant
        val expr = after.trim()
        if (expr.isEmpty()) return null
        return expr.removeSurrounding("\"").ifEmpty { null }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getMacroBody(): String? {
        if (!isFunctionMacro()) return null
        val after = rawAfterName() ?: return null
        val close = matchingParen(after) ?: return null
        return after.substring(close + 1).trim().ifEmpty { null }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getDefineExpressionText(): String? = defineExpression()?.first

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getDefineExpressionOffsetInDirective(): Int = defineExpression()?.second ?: -1

    /**
     * The raw expression of this `#define` together with its start offset inside the directive's text.
     *
     * For a simple macro this is the value following the name; for a function-like macro it is the body
     * after the `(…)` parameter list. Returns `null` when there is no expression. The offset lets the
     * analysis map token spans back to host-editor ranges.
     */
    private fun defineExpression(): Pair<String, Int>? {
        if (!isDefine()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val after = rawAfterName() ?: return null
        val afterOffsetInValue = value.text.length - after.length // start of `after` within the value text

        val (raw, rawOffsetInValue) = when {
            after.startsWith("(") -> {
                val close = matchingParen(after) ?: return null
                (after.substring(close + 1) to afterOffsetInValue + close + 1)
            }
            // Array element assignment `#define Name[Index] Value`: the expression is the value after `]`.
            after.startsWith("[") -> {
                val close = matchingBracket(after) ?: return null
                (after.substring(close + 1) to afterOffsetInValue + close + 1)
            }

            else -> (after to afterOffsetInValue)
        }

        val leadingWs = raw.length - raw.trimStart().length
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return trimmed to (valueOffsetInDirective + rawOffsetInValue + leadingWs)
    }

    /** Index of the `)` matching the `(` at index 0, or `null` if unbalanced. */
    private fun matchingParen(text: String): Int? = matchingDelimiter(text, '(', ')')

    /** Index of the `]` matching the `[` at index 0, or `null` if unbalanced. */
    private fun matchingBracket(text: String): Int? = matchingDelimiter(text, '[', ']')

    /** Index of the `}` matching the `{` at index 0, or `null` if unbalanced. */
    private fun matchingBrace(text: String): Int? = matchingDelimiter(text, '{', '}')

    /** Index of the closing [close] matching the [open] at index 0, or `null` if unbalanced. */
    private fun matchingDelimiter(text: String, open: Char, close: Char): Int? {
        if (text.firstOrNull() != open) return null
        var depth = 0
        for (i in text.indices) {
            when (text[i]) {
                open -> depth++
                close -> {
                    depth--; if (depth == 0) return i
                }
            }
        }
        return null
    }

    // ── #dim / #redim arrays ──────────────────────────────────────────────────

    override fun isDim(): Boolean = keywordEquals("dim")

    override fun isRedim(): Boolean = keywordEquals("redim")

    override fun isArrayDeclaration(): Boolean = isDim() || isRedim()

    override fun getArrayName(): String? {
        if (!isArrayDeclaration()) return null
        return nameNode()?.text
    }

    /**
     * The parsed pieces of a `#dim`/`#redim` value `Name\[Size] [{init}]`: the size text and the optional inline
     * initialiser body, each with its offset inside the directive's text. `null` when this is not an array
     * declaration or there is no `[…]`.
     */
    private fun arrayDeclParts(): ArrayDeclParts? {
        if (!isArrayDeclaration()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val after = rawAfterName() ?: return null
        val afterOffsetInValue = value.text.length - after.length
        val leading = after.length - after.trimStart().length
        val fromBracket = after.substring(leading)
        if (!fromBracket.startsWith("[")) return null
        val close = matchingBracket(fromBracket) ?: return null
        val bracketBase = valueOffsetInDirective + afterOffsetInValue + leading
        val sizeText = fromBracket.substring(1, close)
        val sizeOffset = bracketBase + 1

        val rest = fromBracket.substring(close + 1)
        val restLeading = rest.length - rest.trimStart().length
        val initBlock = rest.substring(restLeading)
        var initText: String? = null
        var initOffset = -1
        if (initBlock.startsWith("{")) {
            val initClose = matchingBrace(initBlock)
            if (initClose != null) {
                initText = initBlock.substring(1, initClose)
                initOffset = bracketBase + close + 1 + restLeading + 1
            }
        }
        return ArrayDeclParts(sizeText, sizeOffset, initText, initOffset)
    }

    override fun getArraySizeText(): String? = arrayDeclParts()?.sizeText
    override fun getArraySizeOffsetInDirective(): Int = arrayDeclParts()?.sizeOffset ?: -1
    override fun getArrayInitializerText(): String? = arrayDeclParts()?.initText
    override fun getArrayInitializerOffsetInDirective(): Int = arrayDeclParts()?.initOffset ?: -1

    override fun isArrayElementDefine(): Boolean = isDefine() && rawAfterName()?.startsWith("[") == true

    /**
     * The parsed pieces of an array element `#define Name\[Index] Value`: the index text and the value text, each
     * with its offset inside the directive's text. `null` when this is not an array element assignment.
     */
    private fun arrayElementParts(): ArrayElementParts? {
        if (!isArrayElementDefine()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val after = rawAfterName() ?: return null
        val afterOffsetInValue = value.text.length - after.length
        val close = matchingBracket(after) ?: return null
        val indexText = after.substring(1, close)
        val indexOffset = valueOffsetInDirective + afterOffsetInValue + 1
        return ArrayElementParts(indexText, indexOffset)
    }

    override fun getDefineArrayName(): String? = if (isArrayElementDefine()) nameNode()?.text else null
    override fun getDefineArrayIndexText(): String? = arrayElementParts()?.indexText
    override fun getDefineArrayIndexOffsetInDirective(): Int = arrayElementParts()?.indexOffset ?: -1

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getMacroParameters(): List<String> {
        if (!isFunctionMacro()) return emptyList()
        val after = rawAfterName() ?: return emptyList()
        val close = matchingParen(after) ?: return emptyList()
        // The parameter list may be spread over several physical lines with a trailing backslash; the
        // continuation is whitespace and must not end up in a parameter name.
        return after.substring(1, close)        // text between '(' and ')'
            .replace(IS_PREPROCESSOR_CONTINUATION, " ")
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /** The names declared as macro parameters (e.g. `a`, `b` in `name(a,b)`) — these are local, not references. */
    private fun macroParameterNames(): Set<String> = getMacroParameters().toSet()

    /**
     * The identifier tokens in the value that act as references to other #defines (free text).
     *
     * Applies to a `#define` value/body and to a `#pragma` whose sub-command takes an expression
     * argument (`str`/`int`) — there the identifiers reference other #defines just like in a #define.
     * Flag-based pragmas (`option`/`parseroption`) carry no references (their `-v+` letters are not names).
     */
    private fun expressionReferenceIdentifiers(): List<ASTNode> {
        // `#if`/`#elif` conditions and `#ifexist`/`#ifnexist` filenames are full ISPP expressions whose
        // identifiers reference other #defines (boolean words / reserved keywords / defined(...) excluded).
        if (isIfElif() || isIfExistFamily()) return ifElifReferenceIdentifiers()
        val isExprDefine = isDefine()
        val isExprPragma = isPragma() && pragmaArgumentType().let {
            it == IsPreprocessorPragmaArgument.STR || it == IsPreprocessorPragmaArgument.INT
        }
        if (!isExprDefine && !isExprPragma) return emptyList()
        val name = nameNode() ?: return emptyList()  // the #define name resp. the #pragma sub-command
        val visibility = visibilityNode()
        val params = if (isExprDefine) macroParameterNames() else emptySet()
        val symbolArgs = symbolArgumentNodes()
        return valueIdentifiers()
            .filter { it !== name }          // not the define's own name / pragma sub-command
            .filter { it !== visibility }    // not a scope/visibility keyword
            .filter { it.text !in params }   // not a macro parameter (declaration or use)
            .filter { it !in symbolArgs }    // not the un-evaluated symbol argument of `Defined(X)` & co.
    }

    /**
     * The value identifiers of a `#dim`/`#redim` that act as references to other #defines: every identifier of
     * the size expression and the inline initialiser, excluding the array name itself and the scope keyword.
     */
    private fun arrayDeclReferenceIdentifiers(): List<ASTNode> {
        if (!isArrayDeclaration()) return emptyList()
        val name = nameNode()
        val visibility = visibilityNode()
        return valueIdentifiers()
            .filter { it !== name }
            .filter { it !== visibility }
    }

    /**
     * Identifiers in an `#if`/`#elif` condition that reference other #defines. Excludes boolean words
     * (`true`/`false`/`yes`/`no` — they get a dedicated boolean warning, never an unresolved error),
     * reserved keywords (`defined`/type keywords from the spec) and the argument of a `defined(...)` call
     * (which may legitimately be undefined), so neither becomes a reference nor an unresolved error.
     */
    private fun ifElifReferenceIdentifiers(): List<ASTNode> {
        val forbidden = service<IsPreprocessorService>().spec.forbiddenVariableNames
            .map { it.name.lowercase() }.toSet()
        val symbolArgs = symbolArgumentNodes()
        return valueIdentifiers()
            .filter { it.text.lowercase() !in IS_PREPROCESSOR_BOOLEAN_WORDS }
            .filter { it.text.lowercase() !in forbidden }
            .filter { it !in symbolArgs }
    }

    /**
     * The identifier nodes that are the un-evaluated *identifier* argument of a built-in call in the value,
     * e.g. `X` in `defined(X)` or `TypeOf(X)`.
     *
     * These built-ins declare their first parameter as `Ident` in the bundled specification: ISPP passes the
     * *name*, not a value, and the name may legitimately be undefined — so it must not become a reference,
     * which would report it as unresolved.
     *
     * An `Array` parameter (`DimOf(A)`) is deliberately **not** included: the array must exist, so its name
     * stays a resolvable reference for navigation, find-usages and rename.
     */
    private fun symbolArgumentNodes(): Set<ASTNode> {
        val value = (this as IsPreprocessorDirective).value?.node ?: return emptySet()
        val preprocessorService = service<IsPreprocessorService>()
        val tokens = value.getChildren(null).filter { it.elementType != TokenType.WHITE_SPACE }
        val result = mutableSetOf<ASTNode>()
        tokens.forEachIndexed { i, token ->
            if (token.elementType != IsPreprocessorTypes.IDENTIFIER) return@forEachIndexed
            if (tokens.getOrNull(i + 1)?.elementType != IsPreprocessorTypes.LPAREN) return@forEachIndexed
            val kind = preprocessorService.builtinSignature(token.text)?.parameters?.firstOrNull()?.kind
            if (kind != IsPreprocessorBuiltinParameterKind.IDENT) return@forEachIndexed
            tokens.getOrNull(i + 2)
                ?.takeIf { it.elementType == IsPreprocessorTypes.IDENTIFIER }
                ?.let { result += it }
        }
        return result
    }

    /**
     * Returns references contributed by this PSI element.
     */
    override fun getReferences(): Array<PsiReference> {
        if (isInclude()) {
            val range = includePathRangeInDirective() ?: return PsiReference.EMPTY_ARRAY
            val path = getIncludePath() ?: return PsiReference.EMPTY_ARRAY
            return arrayOf(IsIncludeFileReference(this as IsPreprocessorDirective, range, path))
        }
        // `#undef Name` references the nearest preceding `#define Name` (for navigation/rename/find-usages).
        // `#ifdef`/`#ifndef Name` reference it likewise, but a missing #define is *not* an error here.
        if (isUndef() || isIfdefFamily()) {
            val name = nameNode() ?: return PsiReference.EMPTY_ARRAY
            val directive = this as IsPreprocessorDirective
            val offset = name.startOffset - directive.textRange.startOffset
            return arrayOf(IsPreprocessorExpressionReference(directive, offset, name.text))
        }
        // `#redim Name[Size]`: the name references the preceding `#dim Name` (missing #dim *is* an error, flagged
        // by the annotator), plus the size-expression references.
        if (isRedim()) {
            val directive = this as IsPreprocessorDirective
            val base = directive.textRange.startOffset
            val refs = mutableListOf<PsiReference>()
            nameNode()?.let { refs += IsPreprocessorExpressionReference(directive, it.startOffset - base, it.text) }
            arrayDeclReferenceIdentifiers().forEach { id ->
                refs += IsPreprocessorExpressionReference(directive, id.startOffset - base, id.text)
            }
            return refs.toTypedArray()
        }
        // `#dim Name[Size] {init…}`: the size and inline-initialiser identifiers reference other #defines.
        if (isDim()) {
            val directive = this as IsPreprocessorDirective
            val base = directive.textRange.startOffset
            return arrayDeclReferenceIdentifiers().map { id ->
                IsPreprocessorExpressionReference(directive, id.startOffset - base, id.text)
            }.toTypedArray()
        }
        // `#define Name[Index] Value`: the name references the `#dim`, plus the index/value expression refs.
        if (isArrayElementDefine()) {
            val directive = this as IsPreprocessorDirective
            val base = directive.textRange.startOffset
            val refs = mutableListOf<PsiReference>()
            nameNode()?.let { refs += IsPreprocessorExpressionReference(directive, it.startOffset - base, it.text) }
            expressionReferenceIdentifiers().forEach { id ->
                refs += IsPreprocessorExpressionReference(directive, id.startOffset - base, id.text)
            }
            return refs.toTypedArray()
        }
        // `#ifexist "file"`/`#ifnexist "file"` carry a literal-path file reference (like #include) in addition
        // to any #define references contained in the filename expression.
        if (isIfExistFamily()) {
            val fileRefs: Array<PsiReference> = existPathRangeInDirective()?.let { range ->
                getExistPath()?.let { path ->
                    arrayOf<PsiReference>(IsIncludeFileReference(this as IsPreprocessorDirective, range, path))
                }
            } ?: emptyArray()
            val exprRefs = expressionReferenceIdentifiers().map { id ->
                val directive = this as IsPreprocessorDirective
                IsPreprocessorExpressionReference(directive, id.startOffset - directive.textRange.startOffset, id.text)
            }
            return fileRefs + exprRefs
        }
        // `#for {Var = Init; Cond; Incr} Body`: identifiers in all four slots reference symbols. The loop
        // variable name resolves locally to its own declaration (scope = this #for); every other identifier
        // resolves globally to a preceding #define/#dim/#sub.
        if (isFor()) {
            val directive = this as IsPreprocessorDirective
            val varName = getForVariableName()
            val varOffset = forVariable()?.second
            val forbidden = service<IsPreprocessorService>().spec.forbiddenVariableNames
                .map { it.name.lowercase() }.toSet()
            val refs = mutableListOf<PsiReference>()
            forReferenceSlots().forEach { (slotText, slotOffset) ->
                IsPreprocessorExprTokenizer.tokenize(slotText)
                    .filter { it.type == IsPreprocessorExprTokenType.IDENT }
                    .forEach { tok ->
                        val lower = tok.text.lowercase()
                        if (lower in IS_PREPROCESSOR_BOOLEAN_WORDS || lower in forbidden) return@forEach
                        val offsetInDirective = slotOffset + tok.start
                        if (offsetInDirective == varOffset) return@forEach // the loop-variable declaration itself
                        refs += if (varName != null && tok.text.equals(varName, ignoreCase = true))
                            IsPreprocessorForVariableReference(directive, offsetInDirective, tok.text)
                        else
                            IsPreprocessorExpressionReference(directive, offsetInDirective, tok.text)
                    }
            }
            return refs.toTypedArray()
        }
        val ids = expressionReferenceIdentifiers()
        if (ids.isEmpty()) return PsiReference.EMPTY_ARRAY
        val directive = this as IsPreprocessorDirective
        val base = directive.textRange.startOffset
        return ids.map { id ->
            IsPreprocessorExpressionReference(directive, id.startOffset - base, id.text)
        }.toTypedArray()
    }

    /** The non-empty `#for` slots (init/cond/incr/body) paired with their offset inside the directive's text. */
    private fun forReferenceSlots(): List<Pair<String, Int>> {
        val parts = forParts() ?: return emptyList()
        return listOfNotNull(
            parts.initText?.takeIf { parts.initOffset >= 0 }?.let { it to parts.initOffset },
            parts.condText?.takeIf { parts.condOffset >= 0 }?.let { it to parts.condOffset },
            parts.incrText?.takeIf { parts.incrOffset >= 0 }?.let { it to parts.incrOffset },
            parts.bodyText?.takeIf { parts.bodyOffset >= 0 }?.let { it to parts.bodyOffset },
        )
    }

    // ── #sub / #endsub ────────────────────────────────────────────────────────

    override fun isSub(): Boolean = keywordEquals("sub")

    override fun isEndsub(): Boolean = keywordEquals("endsub")

    override fun isSubroutineOpener(): Boolean = isSub()

    override fun isSubroutineDirective(): Boolean = isSub() || isEndsub()

    override fun getSubroutineName(): String? {
        if (!isSub()) return null
        return nameNode()?.text
    }

    // ── #for loops ──────────────────────────────────────────────────────────────

    override fun isFor(): Boolean = keywordEquals("for")

    override fun getForInitText(): String? = forParts()?.initText
    override fun getForInitOffsetInDirective(): Int = forParts()?.initOffset ?: -1
    override fun getForConditionText(): String? = forParts()?.condText
    override fun getForConditionOffsetInDirective(): Int = forParts()?.condOffset ?: -1
    override fun getForIncrementText(): String? = forParts()?.incrText
    override fun getForIncrementOffsetInDirective(): Int = forParts()?.incrOffset ?: -1
    override fun getForBodyText(): String? = forParts()?.bodyText
    override fun getForBodyOffsetInDirective(): Int = forParts()?.bodyOffset ?: -1

    override fun getForVariableName(): String? = forVariable()?.first

    override fun getForVariableNameNode(): PsiElement? {
        val offset = forVariable()?.second ?: return null
        val leaf = (this as IsPreprocessorDirective).findElementAt(offset) ?: return null
        return if (leaf.node.elementType == IsPreprocessorTypes.IDENTIFIER) leaf else null
    }

    /**
     * The loop variable of a `#for`: its name plus the offset of its identifier within the directive's text.
     * The variable is the outermost left-hand side of the first assignment in the initializer (`Name = …`,
     * also the first link of a chained `a = b = …`). `null` when this is not a `#for` or the initializer
     * contains no `Name =` assignment.
     */
    private fun forVariable(): Pair<String, Int>? {
        val parts = forParts() ?: return null
        val initText = parts.initText ?: return null
        if (parts.initOffset < 0) return null
        val eq = topLevelAssignmentIndex(initText) ?: return null
        val lhs = initText.substring(0, eq)
        val leading = lhs.length - lhs.trimStart().length
        val name = lhs.trim()
        if (name.isEmpty() || !IS_PREPROCESSOR_IDENTIFIER.matches(name)) return null
        return name to (parts.initOffset + leading)
    }

    /** Parsed `{Init; Cond; Incr} Body` pieces of a `#for`, each with its offset inside the directive's text. */
    private fun forParts(): ForParts? {
        if (!isFor()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val raw = value.text
        val open = raw.indexOf('{')
        if (open < 0) {
            // No brace group: the whole value is treated as the (invalid) body — the annotator reports it.
            val bodyLeading = raw.length - raw.trimStart().length
            val body = raw.trim()
            return ForParts(
                null, -1, null, -1, null, -1,
                body.ifEmpty { null }, if (body.isEmpty()) -1 else valueOffsetInDirective + bodyLeading
            )
        }
        val rel = matchingBrace(raw.substring(open)) ?: return ForParts(
            null, -1, null, -1, null, -1, null, -1
        )
        val close = open + rel
        val inner = raw.substring(open + 1, close)
        val sections = splitTopLevelSemicolons(inner)
        fun section(i: Int): Pair<String, Int>? = sections.getOrNull(i)?.let { (text, startInInner) ->
            text to (valueOffsetInDirective + open + 1 + startInInner)
        }

        val init = section(0)
        val cond = section(1)
        val incr = section(2)
        val after = raw.substring(close + 1)
        val bodyLeading = after.length - after.trimStart().length
        val body = after.trim()
        return ForParts(
            init?.first, init?.second ?: -1,
            cond?.first, cond?.second ?: -1,
            incr?.first, incr?.second ?: -1,
            body.ifEmpty { null }, if (body.isEmpty()) -1 else valueOffsetInDirective + close + 1 + bodyLeading,
        )
    }

    // ── #pragma ───────────────────────────────────────────────────────────────

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isPragma(): Boolean =
        (this as IsPreprocessorDirective).identifier?.text?.equals("pragma", ignoreCase = true) == true

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getPragmaSubCommand(): String? {
        if (!isPragma()) return null
        return nameNode()?.text
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getPragmaArgumentText(): String? = pragmaArgument()?.first

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getPragmaArgumentOffsetInDirective(): Int = pragmaArgument()?.second ?: -1

    /** The raw argument of this `#pragma` (after the sub-command) and its start offset inside the directive. */
    private fun pragmaArgument(): Pair<String, Int>? {
        if (!isPragma()) return null
        val directive = this as IsPreprocessorDirective
        val value = directive.value ?: return null
        val valueOffsetInDirective = value.textRange.startOffset - directive.textRange.startOffset
        val after = rawAfterName() ?: return null
        val afterOffsetInValue = value.text.length - after.length
        val leadingWs = after.length - after.trimStart().length
        val trimmed = after.trim()
        if (trimmed.isEmpty()) return null
        return trimmed to (valueOffsetInDirective + afterOffsetInValue + leadingWs)
    }

    /** Argument kind declared for this `#pragma`'s sub-command in the ISPP spec, or `null` if unknown. */
    private fun pragmaArgumentType(): IsPreprocessorPragmaArgument? {
        val sub = getPragmaSubCommand() ?: return null
        return service<IsPreprocessorService>().spec.pragmaSubCommands
            .firstOrNull { it.name.equals(sub, ignoreCase = true) }?.argument
    }

    // ── #include ──────────────────────────────────────────────────────────────

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isInclude(): Boolean =
        (this as IsPreprocessorDirective).identifier?.text?.equals("include", ignoreCase = true) == true

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getIncludeLiteralString(): IsPreprocessorQuotedString? {
        if (!isInclude()) return null
        val value = (this as IsPreprocessorDirective).value ?: return null
        // Only a single literal string counts (no embedded constants, no surrounding expression tokens).
        if (value.constantList.isNotEmpty()) return null
        val string = value.quotedStringList.singleOrNull() ?: return null
        return if (value.text.trim() == string.text.trim()) string else null
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getIncludePath(): String? =
        getIncludeLiteralString()?.text?.trim()?.removeSurrounding("\"")

    /** The path span of [getIncludeLiteralString] relative to this directive's text, or `null`. */
    private fun includePathRangeInDirective(): TextRange? =
        literalPathRange(getIncludeLiteralString(), getIncludePath())

    /** The single quoted-string literal of the value (no constants, no surrounding expression), or `null`. */
    private fun singleLiteralString(): IsPreprocessorQuotedString? {
        val value = (this as IsPreprocessorDirective).value ?: return null
        if (value.constantList.isNotEmpty()) return null
        val string = value.quotedStringList.singleOrNull() ?: return null
        return if (value.text.trim() == string.text.trim()) string else null
    }

    /** The path span of [string]/[path] (inside the quotes) relative to this directive's text, or `null`. */
    private fun literalPathRange(string: IsPreprocessorQuotedString?, path: String?): TextRange? {
        if (string == null || path == null) return null
        val base = (this as IsPreprocessorDirective).textRange.startOffset
        val start = string.textRange.startOffset - base + 1  // after the opening quote
        return TextRange(start, start + path.length)
    }

    // ── #ifexist / #ifnexist ──────────────────────────────────────────────────

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getExistLiteralString(): IsPreprocessorQuotedString? {
        if (!isIfExistFamily()) return null
        return singleLiteralString()
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getExistPath(): String? =
        getExistLiteralString()?.text?.trim()?.removeSurrounding("\"")

    /** The path span of [getExistLiteralString] relative to this directive's text, or `null`. */
    private fun existPathRangeInDirective(): TextRange? = literalPathRange(getExistLiteralString(), getExistPath())

    /**
     * Resolves the literal `#ifexist`/`#ifnexist` filename to the file it points at (host-relative), or
     * `null`. Foundation for a future existence diagnostic; reuses the shared `#include` path semantics.
     */
    override fun resolveExistFile(): com.intellij.openapi.vfs.VirtualFile? =
        getExistPath()?.let { resolveRelativeFile(it) }

    /**
     * Resolves [path] against the host script's directory with the shared `#include` path semantics — the
     * non-literal counterpart of [resolveExistFile], used once the branch analysis has computed a filename
     * expression to a string.
     */
    override fun resolveRelativeFile(path: String): com.intellij.openapi.vfs.VirtualFile? {
        val injMgr = InjectedLanguageManager.getInstance(project)
        val hostFile = injMgr.getTopLevelFile(containingFile)
        val baseDir = hostFile?.virtualFile?.parent ?: return null
        return IsIncludePaths.resolve(baseDir, path)
    }

    // ── PsiNameIdentifierOwner ────────────────────────────────────────────────

    /**
     * Returns the logical name exposed by this PSI element.
     */
    override fun getName(): String? = getNameIdentifier()?.text

    /**
     * Renames this PSI element and returns the updated element.
     */
    override fun setName(name: String): PsiElement {
        val oldId = getNameIdentifier() ?: return this
        val injManager = InjectedLanguageManager.getInstance(project)
        val hostRange = injManager.injectedToHost(oldId, oldId.textRange)
        val hostFile = injManager.getTopLevelFile(containingFile)
        val docManager = PsiDocumentManager.getInstance(project)
        val doc = docManager.getDocument(hostFile) ?: return this
        // Commit pending PSI→document operations so the document is no longer locked.
        docManager.doPostponedOperationsAndUnblockDocument(doc)
        doc.replaceString(hostRange.startOffset, hostRange.endOffset, name)
        docManager.commitDocument(doc)
        return this
    }

    /**
     * Returns the PSI element that carries the renameable name.
     */
    override fun getNameIdentifier(): PsiElement? {
        // An array element `#define Name[i]` is a *usage* of the array, not a declaration — handled via references.
        if (isArrayElementDefine()) return null
        // A `#sub Name` declares the subroutine name; a `#for {Var = …}` declares its loop variable.
        if (isSub()) return nameNode()?.psi
        if (isFor()) return getForVariableNameNode()
        if (!isDefine() && !isUndef() && !isArrayDeclaration()) return null
        return nameNode()?.psi
    }

    /**
     * Returns the editor offset used for navigation to this PSI element.
     */
    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()

    /** Parsed pieces of a `#dim`/`#redim` value (size + optional inline initialiser), with directive offsets. */
    private data class ArrayDeclParts(
        val sizeText: String,
        val sizeOffset: Int,
        val initText: String?,
        val initOffset: Int,
    )

    /** Parsed pieces of an array element `#define Name\[Index] Value` (the index), with its directive offset. */
    private data class ArrayElementParts(val indexText: String, val indexOffset: Int)

    /** Parsed `{Init; Cond; Incr} Body` pieces of a `#for`, each text with its offset inside the directive. */
    private data class ForParts(
        val initText: String?, val initOffset: Int,
        val condText: String?, val condOffset: Int,
        val incrText: String?, val incrOffset: Int,
        val bodyText: String?, val bodyOffset: Int,
    )

    /**
     * Splits [text] on top-level `;`, ignoring semicolons nested in `()`/`[]`/`{}` or inside `"…"`/`'…'`
     * strings. Returns each section's raw text together with its start offset inside [text].
     */
    private fun splitTopLevelSemicolons(text: String): List<Pair<String, Int>> {
        val parts = mutableListOf<Pair<String, Int>>()
        val sb = StringBuilder()
        var start = 0
        var depth = 0
        var quote: Char? = null
        for (i in text.indices) {
            val c = text[i]
            when {
                quote != null -> {
                    sb.append(c); if (c == quote) quote = null
                }

                c == '"' || c == '\'' -> {
                    sb.append(c); quote = c
                }

                c == '(' || c == '[' || c == '{' -> {
                    depth++; sb.append(c)
                }

                c == ')' || c == ']' || c == '}' -> {
                    depth--; sb.append(c)
                }

                c == ';' && depth == 0 -> {
                    parts += sb.toString() to start; sb.clear(); start = i + 1
                }

                else -> sb.append(c)
            }
        }
        parts += sb.toString() to start
        return parts
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

    private companion object {
        /** A plain ISPP identifier (loop-variable name); letters/underscore start, then letters/digits/underscore. */
        val IS_PREPROCESSOR_IDENTIFIER = Regex("[A-Za-z_][A-Za-z0-9_]*")

        /** A line continuation: a backslash ending the line, plus any spaces/tabs behind it. */
        val IS_PREPROCESSOR_CONTINUATION = Regex("""\\[ \t]*\r?\n""")
    }
}
