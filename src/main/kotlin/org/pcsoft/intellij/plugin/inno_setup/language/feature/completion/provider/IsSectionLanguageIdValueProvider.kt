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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.isInCodeSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionLanguageIdValueProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection) return
        // \[LangOptions] is a directive section (Key=Value), so the value hangs off an
        // IsSectionDirectiveEntry rather than an IsSectionParamPair.
        val paramValue = PsiTreeUtil.getParentOfType(position, IsSectionParamValue::class.java) ?: return
        val directive = paramValue.containingDirectiveEntry ?: return
        val section = directive.containingSection ?: return
        if (!section.nameText.equals("LangOptions", ignoreCase = true)) return
        if (!directive.keyText().equals("LanguageID", ignoreCase = true)) return

        service<IsLanguageDataService>().entries.forEach { lang ->
            val tail = if (lang.builtin) "  Inno built-in" else ""
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(lang.id)        // inserts the "$0409" hex id
                        .withLookupString(lang.displayName)     // also matchable by typing the language name
                        .withPresentableText(lang.displayName)  // shown label, e.g. "English (United States)"
                        .withTailText(tail, true)               // mark Inno-bundled languages
                        .withTypeText(lang.id, true)            // greyed id on the right
                        .withIcon(lang.icon),
                    // Built-in languages are sorted to the top of the popup.
                    if (lang.builtin) 20.0 else 10.0
                )
            )
        }
    }
}
