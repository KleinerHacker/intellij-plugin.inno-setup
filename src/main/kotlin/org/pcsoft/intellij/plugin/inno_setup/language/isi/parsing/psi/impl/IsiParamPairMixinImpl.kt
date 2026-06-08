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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiQuotedString
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

abstract class IsiParamPairMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IsiParamPairEx,
    PsiNameIdentifierOwner {

    private val REFERENCE_KEYS = setOf("tasks", "components", "types", "languages")

    override fun keyText(): String =
        node.findChildByType(IsiTypes.PARAM_KEY)?.psi?.text.orEmpty()

    override fun isNameDeclaration(): Boolean =
        keyText().equals("Name", ignoreCase = true)

    override fun isReferenceParam(): Boolean =
        keyText().lowercase() in REFERENCE_KEYS

    // PsiNameIdentifierOwner — only meaningful for Name: <value> pairs
    override fun getName(): String? {
        if (!isNameDeclaration()) return null
        return (this as IsiParamPair).paramValue?.text?.trim()?.removeSurrounding("\"")
    }

    override fun setName(name: String): PsiElement {
        if (!isNameDeclaration()) return this
        val oldLeaf = nameLeaf() ?: return this
        val quoted = oldLeaf.elementType == IsiTypes.STRING_PART
        // Build the new leaf in a known-valid context (quoted vs. bare) so quotes are preserved.
        val dummyValue = if (quoted) "\"$name\"" else name
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Tasks]\nName: $dummyValue\n")
        val newLeaf = nameLeafOf(PsiTreeUtil.findChildOfType(dummy, IsiParamValue::class.java))?.psi ?: return this
        oldLeaf.psi.replace(newLeaf)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        if (!isNameDeclaration()) return null
        return nameLeaf()?.psi
    }

    /** The leaf carrying the name text: the bare IDENTIFIER, or the STRING_PART inside `"…"`. */
    private fun nameLeaf(): ASTNode? = nameLeafOf((this as IsiParamPair).paramValue)

    private fun nameLeafOf(paramValue: IsiParamValue?): ASTNode? {
        val node = paramValue?.node ?: return null
        node.findChildByType(IsiTypes.IDENTIFIER)?.let { return it }
        return PsiTreeUtil.findChildOfType(paramValue, IsiQuotedString::class.java)
            ?.node?.findChildByType(IsiTypes.STRING_PART)
    }

    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}
