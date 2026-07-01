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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntryEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

/**
 * Reference from a `{cm:MessageName}` constant to the matching \[CustomMessages] declaration.
 *
 * Anchored on the [IsSectionConstantBody]; the range covers only the message-name identifier (after
 * the `cm:` prefix). The reference is soft — the red highlight for an unresolved name is produced
 * explicitly by the annotator (see IsSectionAnnotator.annotateConstant), which keeps it deterministic.
 */
class IsSectionCustomMessageReference(constantBody: IsSectionConstantBody, private val name: String, range: TextRange) :
    PsiReferenceBase<IsSectionConstantBody>(constantBody, range, true) {

    /**
     * Resolves this reference to its target PSI element, or `null` when unresolved.
     */
    override fun resolve(): PsiElement? {
        val issFile = element.containingFile as? IsScriptFile ?: return null
        return issFile.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .firstOrNull { it.customMessageName().equals(name, ignoreCase = true) }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isReferenceTo(element: PsiElement): Boolean {
        val entry = element as? IsSectionDirectiveEntryEx
            ?: PsiTreeUtil.getParentOfType(element, IsSectionDirectiveEntry::class.java) as? IsSectionDirectiveEntryEx
            ?: return false
        return entry.isCustomMessageDeclaration() && entry.customMessageName().equals(name, ignoreCase = true)
    }

    /**
     * Returns completion variants available from this reference.
     */
    override fun getVariants(): Array<Any> {
        val issFile = element.containingFile as? IsScriptFile ?: return emptyArray()
        return issFile.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .mapNotNull { it.customMessageName() }
            .distinct()
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }

    /**
     * Updates the referenced text after the target element has been renamed.
     */
    override fun handleElementRename(newElementName: String): PsiElement {
        val oldId = element.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .firstOrNull { it.startOffset - element.textRange.startOffset == rangeInElement.startOffset }
            ?.psi ?: return element
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IsScriptLanguage, "[Setup]\nAppName={cm:$newElementName}\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsSectionConstantBody::class.java)
            ?.node?.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            ?.lastOrNull()?.psi ?: return element
        oldId.replace(newId)

        return element
    }
}
