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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation.IsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

abstract class IsiParamValueMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    companion object {
        private val REF_KEY_TO_SECTION = mapOf(
            "tasks" to "Tasks",
            "components" to "Components",
            "types" to "Types",
            "languages" to "Languages",
        )
    }

    override fun getReferences(): Array<PsiReference> {
        val pair = containingParamPair ?: return PsiReference.EMPTY_ARRAY
        val targetSection = REF_KEY_TO_SECTION[pair.keyText().lowercase()] ?: return PsiReference.EMPTY_ARRAY
        val paramValue = this as IsiParamValue
        return node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .map { idNode ->
                val start = idNode.startOffset - textOffset
                IsiReference(paramValue, TextRange(start, start + idNode.textLength), targetSection)
            }
            .toTypedArray()
    }
}
