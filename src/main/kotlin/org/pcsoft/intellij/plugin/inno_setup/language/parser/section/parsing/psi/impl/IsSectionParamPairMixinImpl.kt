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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.*

abstract class IsSectionParamPairMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IsSectionParamPairEx,
    PsiNameIdentifierOwner {

    private val REFERENCE_KEYS = setOf("tasks", "components", "types", "languages")

    /**
     * Returns the normalized key text represented by this PSI element.
     */
    override fun keyText(): String =
        node.findChildByType(IsSectionTypes.PARAM_KEY)?.psi?.text.orEmpty()

    /**
     * Returns whether this PSI element declares a named entry.
     */
    override fun isNameDeclaration(): Boolean =
        keyText().equals("Name", ignoreCase = true)

    /**
     * Returns whether this PSI element value references another section entry.
     */
    override fun isReferenceParam(): Boolean =
        keyText().lowercase() in REFERENCE_KEYS

    // PsiNameIdentifierOwner — only meaningful for Name: <value> pairs
    /**
     * Returns the logical name exposed by this PSI element.
     */
    override fun getName(): String? {
        if (!isNameDeclaration()) return null
        return (this as IsSectionParamPair).paramValue?.text?.trim()?.removeSurrounding("\"")
    }

    /**
     * Renames this PSI element and returns the updated element.
     */
    override fun setName(name: String): PsiElement {
        if (!isNameDeclaration()) return this
        val oldLeaf = nameLeaf() ?: return this
        val quoted = oldLeaf.elementType == IsSectionTypes.STRING_PART
        // Build the new leaf in a known-valid context (quoted vs. bare) so quotes are preserved.
        val dummyValue = if (quoted) "\"$name\"" else name
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IsScriptFileType.INSTANCE, "[Tasks]\nName: $dummyValue\n")
        val newLeaf =
            nameLeafOf(PsiTreeUtil.findChildOfType(dummy, IsSectionParamValue::class.java))?.psi ?: return this
        oldLeaf.psi.replace(newLeaf)
        return this
    }

    /**
     * Returns the PSI element that carries the renameable name.
     */
    override fun getNameIdentifier(): PsiElement? {
        if (!isNameDeclaration()) return null
        return nameLeaf()?.psi
    }

    /** The leaf carrying the name text: the bare IDENTIFIER, or the STRING_PART inside `"…"`. */
    private fun nameLeaf(): ASTNode? = nameLeafOf((this as IsSectionParamPair).paramValue)

    private fun nameLeafOf(paramValue: IsSectionParamValue?): ASTNode? {
        val node = paramValue?.node ?: return null
        node.findChildByType(IsSectionTypes.IDENTIFIER)?.let { return it }
        return PsiTreeUtil.findChildOfType(paramValue, IsSectionQuotedString::class.java)
            ?.node?.findChildByType(IsSectionTypes.STRING_PART)
    }

    /**
     * Returns the editor offset used for navigation to this PSI element.
     */
    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}
