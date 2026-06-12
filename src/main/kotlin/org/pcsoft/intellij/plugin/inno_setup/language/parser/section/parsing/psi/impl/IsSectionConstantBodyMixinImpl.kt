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
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionCustomMessageReference
import org.pcsoft.intellij.plugin.inno_setup.language.feature.reference.IsSectionPreprocessorConstantReference
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

abstract class IsSectionConstantBodyMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReferences(): Array<PsiReference> {
        val bodyText = text ?: return PsiReference.EMPTY_ARRAY

        // {#Name} — ISPP define reference.
        if (bodyText.startsWith("#")) {
            val nameNode = node.findChildByType(IsSectionTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
            val name = nameNode.text
            if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
            return arrayOf(IsSectionPreprocessorConstantReference(this as IsSectionConstantBody, name))
        }

        // {cm:Name} — custom-message reference. The name is the first IDENTIFIER after the colon.
        if (bodyText.regionMatches(0, "cm:", 0, 3, ignoreCase = true)) {
            val colon = node.findChildByType(IsSectionTypes.COLON) ?: return PsiReference.EMPTY_ARRAY
            val nameNode = node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
                .firstOrNull { it.startOffset > colon.startOffset } ?: return PsiReference.EMPTY_ARRAY
            val name = nameNode.text
            if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
            val start = nameNode.startOffset - node.startOffset
            return arrayOf(
                IsSectionCustomMessageReference(
                    this as IsSectionConstantBody,
                    name,
                    TextRange(start, start + name.length)
                )
            )
        }

        return PsiReference.EMPTY_ARRAY
    }
}
