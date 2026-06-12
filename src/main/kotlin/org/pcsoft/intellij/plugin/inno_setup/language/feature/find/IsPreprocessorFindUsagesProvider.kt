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
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.IsPreprocessorLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorTypes

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
class IsPreprocessorFindUsagesProvider : FindUsagesProvider {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
        IsPreprocessorLexerAdapter(),
        TokenSet.create(IsPreprocessorTypes.IDENTIFIER),
        TokenSet.EMPTY,
        TokenSet.create(IsPreprocessorTypes.STRING_PART),
    )

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun canFindUsagesFor(element: PsiElement): Boolean =
        element is IsPreprocessorDirectiveEx && element.isDefine()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getHelpId(element: PsiElement): String? = null

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getType(element: PsiElement): String = "ISPP define"

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}
