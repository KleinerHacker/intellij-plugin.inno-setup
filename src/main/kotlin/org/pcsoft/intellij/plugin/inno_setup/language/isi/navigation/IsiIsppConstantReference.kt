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
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

// Anchor is IsiConstantBody (not the IDENTIFIER leaf) so that getReferences() on the
// mixin element is the source of truth. TextRange skips the leading '#' character.
class IsiIsppConstantReference(constantBody: IsiConstantBody, private val name: String) :
    PsiReferenceBase<IsiConstantBody>(constantBody, TextRange(1, 1 + name.length), true) {

    override fun resolve(): PsiElement? {
        val issFile = element.containingFile as? IssFile ?: return null
        return issFile.isppDirectives()
            .firstOrNull { d ->
                (d as? IsppDirectiveEx)?.isDefine() == true &&
                        (d as? IsppDirectiveEx)?.getDefineName() == name
            }
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
        val oldId = element.node.findChildByType(IsiTypes.IDENTIFIER)?.psi ?: return element
        // Create a dummy ISS file with {#Name} in value context to get an IsiTypes.IDENTIFIER node.
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText(
                "dummy.iss", IssFileType.INSTANCE,
                "[Setup]\nAppName={#$newElementName}\n"
            )
        val newId = PsiTreeUtil.findChildOfType(dummy, IsiConstantBody::class.java)
            ?.node?.findChildByType(IsiTypes.IDENTIFIER)?.psi ?: return element
        oldId.replace(newId)
        return element
    }
}
