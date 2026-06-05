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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes
import org.pcsoft.intellij.plugin.inno_setup.language.isi.valueUnquoted
import org.pcsoft.intellij.plugin.inno_setup.language.issFile

// Anchor is IsiParamValue; range points to the specific identifier token within it.
class IsiReference(paramValue: IsiParamValue, range: TextRange, private val targetSection: String) :
    PsiReferenceBase<IsiParamValue>(paramValue, range) {

    override fun resolve(): PsiElement? {
        val file = element.issFile() ?: return null
        val name = element.text.substring(rangeInElement.startOffset, rangeInElement.endOffset)
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .firstOrNull { it.valueUnquoted().equals(name, ignoreCase = true) }
            ?.paramValue
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true
        // element might be the IsiParamPair (the PsiNameIdentifierOwner) containing the resolved paramValue
        val resolvedPair = resolved.parent as? IsiParamPair
        return resolvedPair != null && mgr.areElementsEquivalent(resolvedPair, element)
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val idNode = element.node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .firstOrNull { it.startOffset - element.textOffset == rangeInElement.startOffset }
            ?: return element
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Tasks]\nName: $newElementName\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsiParamValue::class.java)
            ?.node?.findChildByType(IsiTypes.IDENTIFIER)?.psi ?: return element
        idNode.psi.replace(newId)
        return element
    }

    override fun getVariants(): Array<Any> {
        val file = element.issFile() ?: return emptyArray()
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .mapNotNull { it.valueUnquoted().ifEmpty { null } }
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }
}
