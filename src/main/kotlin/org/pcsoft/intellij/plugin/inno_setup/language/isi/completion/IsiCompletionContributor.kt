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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.AttributeKeyProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.BooleanValueProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.CustomMessageAfterCmProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.IsiConstantCompletionProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.IsppVariableAfterHashProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.LanguageIdValueProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.LanguageSectionValueProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.MessagesKeyProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.SectionNameProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider.SectionReferenceValueProvider
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsiTypes.LBRACKET)),
            SectionNameProvider
        )
        // Attribute key completion for all IDENTIFIER positions in ISS files.
        // The provider itself decides whether the cursor is in a key position
        // (IsiParamKey, IsiDirectiveKey, or an orphaned token on an empty line).
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            AttributeKeyProvider
        )
        // Key completion for internationalized sections ([Messages], [CustomMessages]):
        // offers a language-prefix list (flag + name) plus the known message identifiers,
        // and handles the embedded "lang." prefix.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            MessagesKeyProvider
        )
        // Declared custom-message suggestions inside the {cm:…} constant.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            CustomMessageAfterCmProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IsiConstantCompletionProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IsppVariableAfterHashProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            BooleanValueProvider
        )
        // Cross-section reference completion: Tasks: <name>, Components: <name>, etc.
        // ReferenceBasedCompletionContributor does not fire for ISS because the reference
        // lives on IsiParamValue (parent), not the leaf IDENTIFIER. This provider reads
        // IsiReference.getVariants() explicitly for any reference-typed param value.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            SectionReferenceValueProvider
        )
        // Built-in language suggestions for [Languages] Name and MessagesFile parameters.
        // Registered for any element (not just IDENTIFIER) so it also fires inside quoted strings.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            LanguageSectionValueProvider
        )
        // Windows language identifier suggestions for the [LangOptions] LanguageID directive.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            LanguageIdValueProvider
        )
    }
}


