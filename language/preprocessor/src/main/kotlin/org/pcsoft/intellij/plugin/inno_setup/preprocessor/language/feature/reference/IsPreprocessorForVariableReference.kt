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
 * A use of a `#for` loop variable inside the loop's own condition/increment/body.
 *
 * The variable is **scoped to its `#for`**: it resolves to the loop-variable identifier declared in the same
 * directive's initializer (and nowhere else), so navigation/rename/find-usages stay confined to that line.
 * The reference is `soft` so a not-yet-declared variable produces no error.
 */
class IsPreprocessorForVariableReference(
    directive: IsPreprocessorDirective,
    rangeStartInDirective: Int,
    private val name: String,
) : PsiReferenceBase<IsPreprocessorDirective>(
    directive,
    TextRange(rangeStartInDirective, rangeStartInDirective + name.length),
    /* soft = */ true,
) {

    /**
     * Resolves to the owning `#for` directive (a [com.intellij.psi.PsiNameIdentifierOwner] whose name is the
     * loop variable), so navigation, rename and Find Usages operate on the same declaration element — just
     * like the `#define` expression reference.
     */
    override fun resolve(): PsiElement? {
        val ex = element as? IsPreprocessorDirectiveEx ?: return null
        if (!ex.getForVariableName().equals(name, ignoreCase = true)) return null
        return element
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true
        val nameId = (resolved as? com.intellij.psi.PsiNameIdentifierOwner)?.nameIdentifier
        return nameId != null && mgr.areElementsEquivalent(nameId, element)
    }

    override fun getVariants(): Array<Any> = emptyArray()

    /** Renames the in-text occurrence (the declaration is renamed via the owning directive's `setName`). */
    override fun handleElementRename(newElementName: String): PsiElement {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val range = rangeInElement.shiftRight(element.textRange.startOffset)
        val hostRange = injMgr.injectedToHost(element, range)
        val hostFile = injMgr.getTopLevelFile(element.containingFile)
        val docManager = PsiDocumentManager.getInstance(element.project)
        val doc = docManager.getDocument(hostFile) ?: return element
        docManager.doPostponedOperationsAndUnblockDocument(doc)
        doc.replaceString(hostRange.startOffset, hostRange.endOffset, newElementName)
        docManager.commitDocument(doc)
        return element
    }
}
