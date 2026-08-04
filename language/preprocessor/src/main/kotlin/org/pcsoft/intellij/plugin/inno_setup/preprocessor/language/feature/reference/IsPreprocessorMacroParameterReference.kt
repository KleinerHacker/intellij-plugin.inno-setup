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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx

/**
 * A use of a function-like macro parameter inside the macro's own body (`#define M(A) A + 1`).
 *
 * The parameter is **scoped to its `#define`**: it resolves to the parameter declaration of the same
 * directive (an [IsPreprocessorMacroParameterElement]) and nowhere else, so navigation, rename and Find Usages
 * stay confined to that line — and never touch the macro name itself. The reference is `soft`, so a name that
 * is not a parameter is left to the normal `#define` resolution.
 */
class IsPreprocessorMacroParameterReference(
    directive: IsPreprocessorDirective,
    rangeStartInDirective: Int,
    private val name: String,
) : PsiReferenceBase<IsPreprocessorDirective>(
    directive,
    TextRange(rangeStartInDirective, rangeStartInDirective + name.length),
    /* soft = */ true,
) {

    /** Resolves to the declaration of the parameter in the owning `#define`, or `null` when there is none. */
    override fun resolve(): PsiElement? {
        val ex = element as? IsPreprocessorDirectiveEx ?: return null
        val listOffset = ex.getMacroParameterListOffsetInDirective()
        if (listOffset < 0) return null
        val parameter = ex.getMacroParameterDeclarations().firstOrNull { it.name == name } ?: return null
        if (parameter.offset < 0) return null
        return IsPreprocessorMacroParameterElement(element, parameter.name, listOffset + parameter.offset)
    }

    /** Whether [element] is the declaration this reference points at. */
    override fun isReferenceTo(element: PsiElement): Boolean =
        resolve()?.isEquivalentTo(element) == true

    /** Completion variants are contributed by the completion providers, not by this reference. */
    override fun getVariants(): Array<Any> = emptyArray()

    /** Renames the in-text occurrence; the declaration is renamed through the parameter element itself. */
    override fun handleElementRename(newElementName: String): PsiElement {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val range = rangeInElement.shiftRight(element.textRange.startOffset)
        val hostRange = injMgr.injectedToHost(element, range)
        val hostFile = injMgr.getTopLevelFile(element.containingFile)
        val docManager = PsiDocumentManager.getInstance(element.project)
        val document = docManager.getDocument(hostFile) ?: return element
        docManager.doPostponedOperationsAndUnblockDocument(document)
        document.replaceString(hostRange.startOffset, hostRange.endOffset, newElementName)
        docManager.commitDocument(document)
        return element
    }
}
