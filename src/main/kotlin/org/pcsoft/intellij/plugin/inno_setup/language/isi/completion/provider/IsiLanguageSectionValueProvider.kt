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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.isInCodeSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiQuotedString
import org.pcsoft.intellij.plugin.inno_setup.services.IssLanguageService

object IsiLanguageSectionValueProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsiParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair() ?: return
        val section = pair.containingSection() ?: return
        if (!section.nameText().equals("Languages", ignoreCase = true)) return

        // STRING_PART tokens inside IsiQuotedString don't get automatic prefix computation,
        // so strip the dummy identifier manually to set the correct prefix.
        val adjustedResult = if (PsiTreeUtil.getParentOfType(position, IsiQuotedString::class.java) != null) {
            val tokenText = position.text
            val dummyIdx = tokenText.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
            val typed = if (dummyIdx >= 0) tokenText.substring(0, dummyIdx) else tokenText
            result.withPrefixMatcher(typed)
        } else result

        val builtin = service<IssLanguageService>().builtinLanguages
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