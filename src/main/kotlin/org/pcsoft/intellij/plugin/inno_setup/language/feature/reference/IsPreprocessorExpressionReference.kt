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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx

/**
 * A free-text identifier inside a `#define` expression that refers to another `#define`.
 *
 * Resolution is **declaration-order aware**: only `#define`s declared on a line *before* the line
 * holding this reference are considered (forward references do not resolve). The reference is `soft`
 * so unresolved identifiers (e.g. ISPP built-in functions) do not produce an error.
 *
 * Anchored on the [IsPreprocessorDirective]; [rangeInElement] points at the identifier within the directive.
 */
class IsPreprocessorExpressionReference(
    directive: IsPreprocessorDirective,
    rangeStartInDirective: Int,
    private val name: String,
) : PsiReferenceBase<IsPreprocessorDirective>(
    directive,
    TextRange(rangeStartInDirective, rangeStartInDirective + name.length),
    /* soft = */ true,
) {

    override fun resolve(): PsiElement? {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val issFile = injMgr.getTopLevelFile(element.containingFile) as? IsScriptFile ?: return null
        val hostLine = injMgr.getInjectionHost(element) ?: return null
        val currentOffset = hostLine.textRange.startOffset

        return issFile.isppDirectivesWithHostOffset
            .filter { (d, offset) ->
                offset < currentOffset &&
                        (d as? IsPreprocessorDirectiveEx)?.isDefine() == true &&
                        (d as? IsPreprocessorDirectiveEx)?.getDefineName() == name
            }
            .maxByOrNull { it.second }   // nearest preceding declaration
            ?.first
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true

        val nameId = (resolved as? PsiNameIdentifierOwner)?.nameIdentifier

        return nameId != null && mgr.areElementsEquivalent(nameId, element)
    }

    override fun getVariants(): Array<Any> = emptyArray()

    override fun handleElementRename(newElementName: String): PsiElement {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val range = rangeInElement.shiftRight(element.textRange.startOffset) // injected coords
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
