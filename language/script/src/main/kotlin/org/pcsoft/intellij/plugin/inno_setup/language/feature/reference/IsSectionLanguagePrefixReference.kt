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

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.valueUnquoted

/**
 * Reference from a `lang.` key prefix in an internationalized section (\[Messages]/\[CustomMessages])
 * to the matching `\[Languages] Name` declaration. Anchored on the [IsSectionDirectiveEntry]; the range
 * covers only the prefix segment of the key token. Soft — the red highlight for an unknown prefix is
 * produced by the annotator.
 */
class IsSectionLanguagePrefixReference(entry: IsSectionDirectiveEntry, private val prefix: String, range: TextRange) :
    PsiReferenceBase<IsSectionDirectiveEntry>(entry, range, true) {

    /**
     * Resolves this reference to its target PSI element, or `null` when unresolved.
     */
    override fun resolve(): PsiElement? {
        val file = element.containingFile as? IsScriptFile ?: return null

        return file.findSections("Languages")
            .flatMap { it.nameDeclarations }
            .firstOrNull { it.valueUnquoted.equals(prefix, ignoreCase = true) }
            ?.paramValue
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true

        val resolvedPair = resolved.parent as? IsSectionParamPair

        return resolvedPair != null && mgr.areElementsEquivalent(resolvedPair, element)
    }

    /**
     * Returns completion variants available from this reference.
     */
    override fun getVariants(): Array<Any> = emptyArray()

    /**
     * Updates the referenced text after the target element has been renamed.
     */
    override fun handleElementRename(newElementName: String): PsiElement {
        val keyNode = element.directiveKey.node.findChildByType(IsSectionTypes.IDENTIFIER) ?: return element
        val full = keyNode.text
        val dot = full.indexOf('.')
        val newFull = if (dot >= 0) newElementName + full.substring(dot) else newElementName
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IsScriptLanguage, "[Messages]\n$newFull=x\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsSectionDirectiveKey::class.java)
            ?.node?.findChildByType(IsSectionTypes.IDENTIFIER)?.psi ?: return element
        keyNode.psi.replace(newId)

        return element
    }
}
