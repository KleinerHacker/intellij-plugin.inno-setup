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
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntryEx
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

/**
 * Reference from a `{cm:MessageName}` constant to the matching [CustomMessages] declaration.
 *
 * Anchored on the [IsiConstantBody]; the range covers only the message-name identifier (after
 * the `cm:` prefix). The reference is soft — the red highlight for an unresolved name is produced
 * explicitly by the annotator (see IsiAnnotator.annotateConstant), which keeps it deterministic.
 */
class IsiCustomMessageReference(constantBody: IsiConstantBody, private val name: String, range: TextRange) :
    PsiReferenceBase<IsiConstantBody>(constantBody, range, true) {

    override fun resolve(): PsiElement? {
        val issFile = element.containingFile as? IssFile ?: return null
        return issFile.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .firstOrNull { it.customMessageName().equals(name, ignoreCase = true) }
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val entry = element as? IsiDirectiveEntryEx
            ?: PsiTreeUtil.getParentOfType(element, IsiDirectiveEntry::class.java) as? IsiDirectiveEntryEx
            ?: return false
        return entry.isCustomMessageDeclaration() && entry.customMessageName().equals(name, ignoreCase = true)
    }

    override fun getVariants(): Array<Any> {
        val issFile = element.containingFile as? IssFile ?: return emptyArray()
        return issFile.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .mapNotNull { it.customMessageName() }
            .distinct()
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val oldId = element.node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .firstOrNull { it.startOffset - element.textRange.startOffset == rangeInElement.startOffset }
            ?.psi ?: return element
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Setup]\nAppName={cm:$newElementName}\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsiConstantBody::class.java)
            ?.node?.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            ?.lastOrNull()?.psi ?: return element
        oldId.replace(newId)
        return element
    }
}
