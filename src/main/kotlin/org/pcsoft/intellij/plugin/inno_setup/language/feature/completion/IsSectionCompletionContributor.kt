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
import org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.*
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

/**
 * Registers completion providers for the relevant Inno Setup language context.
 */
class IsSectionCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsSectionTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsSectionTypes.LBRACKET)),
            IsSectionNameProvider
        )
        // Attribute key completion for all IDENTIFIER positions in ISS files.
        // The provider itself decides whether the cursor is in a key position
        // (IsSectionParamKey, IsSectionDirectiveKey, or an orphaned token on an empty line).
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsSectionTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionAttributeKeyProvider
        )
        // Key completion for internationalized sections (\[Messages], \[CustomMessages]):
        // offers a language-prefix list (flag + name) plus the known message identifiers,
        // and handles the embedded "lang." prefix.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsSectionTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionMessagesKeyProvider
        )
        // Declared custom-message suggestions inside the {cm:…} constant.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionCustomMessageAfterCmProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionConstantCompletionProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionPreprocessorVariableAfterHashProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionBooleanValueProvider
        )
        // Flag suggestions for `kind: flags` attributes (e.g. [Files] Flags:). Offered after the
        // ':'/'=' and after each already typed flag; whitespace between flags is ignored.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionFlagValueProvider
        )
        // Cross-section reference completion: Tasks: <name>, Components: <name>, etc.
        // ReferenceBasedCompletionContributor does not fire for ISS because the reference
        // lives on IsSectionParamValue (parent), not the leaf IDENTIFIER. This provider reads
        // IsSectionReference.getVariants() explicitly for any reference-typed param value.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsSectionTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionReferenceValueProvider
        )
        // Built-in language suggestions for the \[Languages] MessagesFile parameter (the Name is
        // user-chosen, so it intentionally gets no built-in suggestion list).
        // Registered for any element (not just IDENTIFIER) so it also fires inside quoted strings.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionLanguageSectionValueProvider
        )
        // Environment variable name completion inside {%…} constants.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionEnvVarAfterPercentProvider
        )
        // Windows language identifier suggestions for the \[LangOptions] LanguageID directive.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IsScriptFile::class.java)),
            IsSectionLanguageIdValueProvider
        )
    }
}


