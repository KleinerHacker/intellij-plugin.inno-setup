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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.issFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamPair
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.valueUnquoted

// Anchor is IsSectionParamValue; range points to the specific identifier token within it.
/**
 * Represents a PSI reference used for navigation, rename, and find-usages support.
 */
class IsSectionReference(paramValue: IsSectionParamValue, range: TextRange, private val targetSection: String) :
    PsiReferenceBase<IsSectionParamValue>(paramValue, range) {

    /**
     * Resolves this reference to its target PSI element, or `null` when unresolved.
     */
    override fun resolve(): PsiElement? {
        val file = element.issFile ?: return null
        val name = element.text.substring(rangeInElement.startOffset, rangeInElement.endOffset)

        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations }
            .firstOrNull { it.valueUnquoted.equals(name, ignoreCase = true) }
            ?.paramValue
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true

        // element might be the IsSectionParamPair (the PsiNameIdentifierOwner) containing the resolved paramValue
        val resolvedPair = resolved.parent as? IsSectionParamPair

        return resolvedPair != null && mgr.areElementsEquivalent(resolvedPair, element)
    }

    /**
     * Updates the referenced text after the target element has been renamed.
     */
    override fun handleElementRename(newElementName: String): PsiElement {
        val idNode = element.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .firstOrNull { it.startOffset - element.textOffset == rangeInElement.startOffset }
            ?: return element
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IsScriptLanguage, "[Tasks]\nName: $newElementName\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsSectionParamValue::class.java)
            ?.node?.findChildByType(IsSectionTypes.IDENTIFIER)?.psi ?: return element
        idNode.psi.replace(newId)

        return element
    }

    /**
     * Returns completion variants available from this reference.
     */
    override fun getVariants(): Array<Any> {
        val file = element.issFile ?: return emptyArray()

        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations }
            .mapNotNull { it.valueUnquoted.ifEmpty { null } }
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }
}
