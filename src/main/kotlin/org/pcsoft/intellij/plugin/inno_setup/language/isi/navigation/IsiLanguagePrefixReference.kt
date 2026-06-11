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

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes
import org.pcsoft.intellij.plugin.inno_setup.language.isi.valueUnquoted

/**
 * Reference from a `lang.` key prefix in an internationalized section ([Messages]/[CustomMessages])
 * to the matching `[Languages] Name` declaration. Anchored on the [IsiDirectiveEntry]; the range
 * covers only the prefix segment of the key token. Soft — the red highlight for an unknown prefix is
 * produced by the annotator.
 */
class IsiLanguagePrefixReference(entry: IsiDirectiveEntry, private val prefix: String, range: TextRange) :
    PsiReferenceBase<IsiDirectiveEntry>(entry, range, true) {

    override fun resolve(): PsiElement? {
        val file = element.containingFile as? IssFile ?: return null

        return file.findSections("Languages")
            .flatMap { it.nameDeclarations }
            .firstOrNull { it.valueUnquoted.equals(prefix, ignoreCase = true) }
            ?.paramValue
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true

        val resolvedPair = resolved.parent as? IsiParamPair

        return resolvedPair != null && mgr.areElementsEquivalent(resolvedPair, element)
    }

    override fun getVariants(): Array<Any> = emptyArray()

    override fun handleElementRename(newElementName: String): PsiElement {
        val keyNode = element.directiveKey.node.findChildByType(IsiTypes.IDENTIFIER) ?: return element
        val full = keyNode.text
        val dot = full.indexOf('.')
        val newFull = if (dot >= 0) newElementName + full.substring(dot) else newElementName
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Messages]\n$newFull=x\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IsiDirectiveKey::class.java)
            ?.node?.findChildByType(IsiTypes.IDENTIFIER)?.psi ?: return element
        keyNode.psi.replace(newId)

        return element
    }
}
