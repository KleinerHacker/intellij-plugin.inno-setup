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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionCustomMessageReference
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionPreprocessorConstantReference
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.customMessageNameRange
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

abstract class IsSectionConstantBodyMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    /**
     * Returns references contributed by this PSI element.
     */
    override fun getReferences(): Array<PsiReference> {
        val bodyText = text ?: return PsiReference.EMPTY_ARRAY

        // {#Name} — ISPP define reference.
        if (bodyText.startsWith("#")) {
            val nameNode = node.findChildByType(IsSectionTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
            val name = nameNode.text
            if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
            return arrayOf(IsSectionPreprocessorConstantReference(this as IsSectionConstantBody, name))
        }

        // {cm:Name} or {cm:Name,Arg1,…} — custom-message reference covering the name segment.
        val cm = (this as IsSectionConstantBody).customMessageNameRange()
        if (cm != null) {
            val (name, range) = cm
            return arrayOf(IsSectionCustomMessageReference(this, name, range))
        }

        return PsiReference.EMPTY_ARRAY
    }
}
