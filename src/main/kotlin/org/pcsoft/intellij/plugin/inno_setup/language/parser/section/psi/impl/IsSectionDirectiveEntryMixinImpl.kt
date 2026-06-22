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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionLanguagePrefixReference
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntryEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.specSection

/**
 * Mixin for `Key=Value` directive entries. In a \[CustomMessages] section, the entry declares a
 * custom message: its name is the key with any `lang.` prefix stripped, and it is renamable.
 * Outside \[CustomMessages] the [com.intellij.psi.PsiNameIdentifierOwner] hooks return `null`, so
 * ordinary directive entries are unaffected.
 */
abstract class IsSectionDirectiveEntryMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IsSectionDirectiveEntryEx {

    /**
     * Returns the normalized key text represented by this PSI element.
     */
    override fun keyText(): String = (this as IsSectionDirectiveEntry).directiveKey.text.trim()

    /**
     * Returns whether this entry declares a custom message.
     */
    override fun isCustomMessageDeclaration(): Boolean =
        containingSection?.nameText?.equals("CustomMessages", ignoreCase = true) == true

    /**
     * Returns the custom-message name declared by this entry, if any.
     */
    override fun customMessageName(): String? {
        if (!isCustomMessageDeclaration()) return null
        return keyText().substringAfterLast('.').ifEmpty { null }
    }

    // PsiNameIdentifierOwner — only meaningful for \[CustomMessages] entries.
    /**
     * Returns the logical name exposed by this PSI element.
     */
    override fun getName(): String? = customMessageName()

    /**
     * Returns the PSI element that carries the renameable name.
     */
    override fun getNameIdentifier(): PsiElement? {
        if (!isCustomMessageDeclaration()) return null
        return (this as IsSectionDirectiveEntry).directiveKey.node.findChildByType(IsSectionTypes.IDENTIFIER)?.psi
    }

    /**
     * Renames the message identifier, preserving any `lang.` prefix, and applies the same rename
     * to every other-language declaration of the same message in the file so all localized
     * variants stay in sync. The `{cm:…}` usages are updated separately by the rename framework
     * via the reference's `handleElementRename`.
     */
    override fun setName(name: String): PsiElement {
        val oldName = customMessageName() ?: return this
        val file = containingFile as? IsScriptFile ?: return this
        file.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .filter { it.customMessageName().equals(oldName, ignoreCase = true) }
            .forEach { renameKeyPart(it, name) }
        return this
    }

    /**
     * In an internationalized section (\[Messages]/\[CustomMessages]) a `lang.` key prefix references
     * the matching `\[Languages] Name`. The reference covers only the prefix segment of the key.
     */
    override fun getReferences(): Array<PsiReference> {
        if (containingSection?.specSection?.internationalization != true) return PsiReference.EMPTY_ARRAY
        val entry = this as IsSectionDirectiveEntry
        val keyNode =
            entry.directiveKey.node.findChildByType(IsSectionTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
        val dot = keyNode.text.indexOf('.')
        if (dot <= 0) return PsiReference.EMPTY_ARRAY
        val start = keyNode.startOffset - node.startOffset
        return arrayOf(
            IsSectionLanguagePrefixReference(
                entry,
                keyNode.text.substring(0, dot),
                TextRange(start, start + dot)
            )
        )
    }

    /**
     * Returns the editor offset used for navigation to this PSI element.
     */
    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()

    private fun renameKeyPart(entry: IsSectionDirectiveEntry, newName: String) {
        val idNode = entry.directiveKey.node.findChildByType(IsSectionTypes.IDENTIFIER) ?: return
        val full = idNode.text
        val dot = full.lastIndexOf('.')
        val newFull = if (dot >= 0) full.substring(0, dot + 1) + newName else newName
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IsScriptFileType.INSTANCE, "[CustomMessages]\n$newFull=x\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsSectionDirectiveKey::class.java)
            ?.node?.findChildByType(IsSectionTypes.IDENTIFIER)?.psi ?: return
        idNode.psi.replace(newId)
    }
}
