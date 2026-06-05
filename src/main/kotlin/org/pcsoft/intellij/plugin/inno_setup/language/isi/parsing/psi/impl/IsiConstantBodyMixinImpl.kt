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
import com.intellij.psi.PsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation.IsiIsppConstantReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

abstract class IsiConstantBodyMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReferences(): Array<PsiReference> {
        val bodyText = text ?: return PsiReference.EMPTY_ARRAY
        if (!bodyText.startsWith("#")) return PsiReference.EMPTY_ARRAY
        val nameNode = node.findChildByType(IsiTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
        val name = nameNode.text
        if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
        return arrayOf(IsiIsppConstantReference(this as IsiConstantBody, name))
    }
}
