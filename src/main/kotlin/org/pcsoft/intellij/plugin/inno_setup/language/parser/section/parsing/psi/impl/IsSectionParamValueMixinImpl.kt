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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionReference
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

abstract class IsSectionParamValueMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    companion object {
        private val REF_KEY_TO_SECTION = mapOf(
            "tasks" to "Tasks",
            "components" to "Components",
            "types" to "Types",
            "languages" to "Languages",
        )
    }

    /**
     * Returns references contributed by this PSI element.
     */
    override fun getReferences(): Array<PsiReference> {
        val pair = containingParamPair ?: return PsiReference.EMPTY_ARRAY
        val targetSection = REF_KEY_TO_SECTION[pair.keyText().lowercase()] ?: return PsiReference.EMPTY_ARRAY
        val paramValue = this as IsSectionParamValue
        return node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .map { idNode ->
                val start = idNode.startOffset - textOffset
                IsSectionReference(paramValue, TextRange(start, start + idNode.textLength), targetSection)
            }
            .toTypedArray()
    }
}
