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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
class IsPreprocessorInjector : MultiHostInjector {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is IsSectionPreprocessorLine) return

        val lineNode = context.node.findChildByType(IsSectionTypes.HASH_LINE) ?: return
        val startInHost = lineNode.startOffset - context.textRange.startOffset
        registrar.startInjecting(IsPreprocessorLanguage)
            .addPlace(
                null, null, context as PsiLanguageInjectionHost,
                TextRange(startInHost, startInHost + lineNode.textLength)
            )
            .doneInjecting()
    }

    /**
     * Provides Inno Setup plugin behavior for the IntelliJ Platform.
     */
    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(IsSectionPreprocessorLine::class.java)
}
