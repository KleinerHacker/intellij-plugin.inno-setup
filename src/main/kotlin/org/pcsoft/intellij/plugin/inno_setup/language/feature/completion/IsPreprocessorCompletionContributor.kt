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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDefineExpressionProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDirectiveKeywordProvider
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorTypes

class IsPreprocessorCompletionContributor : CompletionContributor() {
    init {
        // Directive keyword after #
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsPreprocessorTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsPreprocessorTypes.HASH))
                .withParent(IsPreprocessorDirective::class.java),
            IsPreprocessorDirectiveKeywordProvider
        )
        // Names of preceding #defines inside a #define expression
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorDefineExpressionProvider
        )
    }
}
