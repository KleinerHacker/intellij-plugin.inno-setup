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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsIncludeFileReference
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsPreprocessorExpressionReference
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorQuotedString
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes

abstract class IsPreprocessorDirectiveMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IsPreprocessorDirectiveEx {

    private fun valueIdentifiers(): Array<ASTNode> =
        (this as IsPreprocessorDirective).value
            ?.node?.getChildren(TokenSet.create(IsPreprocessorTypes.IDENTIFIER))
            ?: emptyArray()

    private fun nameNode(): ASTNode? = valueIdentifiers().firstOrNull()

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

        val (raw, rawOffsetInValue) = if (after.startsWith("(")) {
            val close = matchingParen(after) ?: return null
            (after.substring(close + 1) to afterOffsetInValue + close + 1)
        } else {
            (after to afterOffsetInValue)
        }

        val leadingWs = raw.length - raw.trimStart().length
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return trimmed to (valueOffsetInDirective + rawOffsetInValue + leadingWs)
    }

    /** Index of the `)` matching the `(` at index 0, or `null` if unbalanced. */
    private fun matchingParen(text: String): Int? {
        var depth = 0
        for (i in text.indices) {
            when (text[i]) {
                '(' -> depth++
                ')' -> {
                    depth--; if (depth == 0) return i
                }
            }
        }
        return null
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getMacroParameters(): List<String> {
        if (!isFunctionMacro()) return emptyList()
        val after = rawAfterName() ?: return emptyList()
        val close = matchingParen(after) ?: return emptyList()
        return after.substring(1, close)        // text between '(' and ')'
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /** The names declared as macro parameters (e.g. `a`, `b` in `name(a,b)`) — these are local, not references. */
    private fun macroParameterNames(): Set<String> = getMacroParameters().toSet()

    /** The identifier tokens in the value that act as references to other #defines (free text). */
    private fun expressionReferenceIdentifiers(): List<ASTNode> {
        if (!isDefine()) return emptyList()
        val name = nameNode() ?: return emptyList()
        val params = macroParameterNames()
        return valueIdentifiers()
            .filter { it !== name }          // not the define's own name
            .filter { it.text !in params }   // not a macro parameter (declaration or use)
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
        val ids = expressionReferenceIdentifiers()
        if (ids.isEmpty()) return PsiReference.EMPTY_ARRAY
        val directive = this as IsPreprocessorDirective
        val base = directive.textRange.startOffset
        return ids.map { id ->
            IsPreprocessorExpressionReference(directive, id.startOffset - base, id.text)
        }.toTypedArray()
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
    private fun includePathRangeInDirective(): TextRange? {
        val string = getIncludeLiteralString() ?: return null
        val path = getIncludePath() ?: return null
        val base = (this as IsPreprocessorDirective).textRange.startOffset
        val start = string.textRange.startOffset - base + 1  // after the opening quote
        return TextRange(start, start + path.length)
    }

    // ── PsiNameIdentifierOwner ────────────────────────────────────────────────

    /**
     * Returns the logical name exposed by this PSI element.
     */
    override fun getName(): String? = getDefineName()

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
        if (!isDefine()) return null
        return nameNode()?.psi
    }

    /**
     * Returns the editor offset used for navigation to this PSI element.
     */
    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}
