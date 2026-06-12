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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.isInCodeSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionQuotedString
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionLanguageSectionValueProvider : CompletionProvider<CompletionParameters>() {
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
        val paramValue = PsiTreeUtil.getParentOfType(position, IsSectionParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair ?: return
        val section = pair.containingSection ?: return
        if (!section.nameText.equals("Languages", ignoreCase = true)) return

        // STRING_PART tokens inside IsSectionQuotedString don't get automatic prefix computation,
        // so strip the dummy identifier manually to set the correct prefix.
        val adjustedResult = if (PsiTreeUtil.getParentOfType(position, IsSectionQuotedString::class.java) != null) {
            val tokenText = position.text
            val dummyIdx = tokenText.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
            val typed = if (dummyIdx >= 0) tokenText.substring(0, dummyIdx) else tokenText
            result.withPrefixMatcher(typed)
        } else result

        val builtin = service<IsLanguageDataService>().builtinLanguages
        val key = pair.keyText()
        when {
            key.equals("Name", ignoreCase = true) ->
                builtin.forEach { lang ->
                    val issName = lang.issName ?: return@forEach
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(issName)
                                .withTypeText(lang.displayName)
                                .withIcon(lang.icon),
                            10.0
                        )
                    )
                }

            key.equals("MessagesFile", ignoreCase = true) ->
                builtin.forEach { lang ->
                    val messagesFile = lang.messagesFile ?: return@forEach
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(messagesFile)
                                .withTypeText(lang.displayName)
                                .withIcon(lang.icon),
                            10.0
                        )
                    )
                }
        }
    }
}
