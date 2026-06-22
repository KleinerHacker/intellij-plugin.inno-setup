/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorBuiltinFunctionProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDefineExpressionProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDefineNameProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDirectiveKeywordProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorIncludeFileProvider
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorPragmaProvider
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes

/**
 * Registers completion providers for the relevant Inno Setup language context.
 */
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
        // Scope/visibility keywords (and, for #undef, existing define names) in the name position
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorDefineNameProvider
        )
        // Names of preceding #defines inside a #define expression
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorDefineExpressionProvider
        )
        // Built-in ISPP functions inside a #define expression
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorBuiltinFunctionProvider
        )
        // File names inside an #include "…" string
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorIncludeFileProvider
        )
        // #pragma sub-commands and option flags
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsPreprocessorLanguage),
            IsPreprocessorPragmaProvider
        )
    }
}
