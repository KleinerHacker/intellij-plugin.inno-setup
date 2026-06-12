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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.find

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.IsSectionLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionDirectiveEntryEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

class IsSectionFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
        IsSectionLexerAdapter(),
        TokenSet.create(IsSectionTypes.IDENTIFIER),
        TokenSet.create(IsSectionTypes.COMMENT),
        TokenSet.create(IsSectionTypes.STRING_PART),
    )

    override fun canFindUsagesFor(element: PsiElement): Boolean =
        (element is IsSectionParamPairEx && element.isNameDeclaration())
                || (element is IsSectionDirectiveEntryEx && element.isCustomMessageDeclaration())

    override fun getHelpId(element: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when {
        element is IsSectionParamPairEx && element.isNameDeclaration() -> "ISS named item"
        element is IsSectionDirectiveEntryEx && element.isCustomMessageDeclaration() -> "ISS custom message"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}
