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
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.allowedInLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.appliesTo

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionNameProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val file = parameters.originalFile as? IsScriptFile ?: return
        val target = file.specTarget
        // In .isl language files only the language-file sections are offered.
        val specSections = service<IsSpecService>().spec.sections
            .filter { file !is IsLanguageFile || it.allowedInLanguageFile }

        val existingNames = file.sections
            .map { it.nameText.lowercase() }
            .toSet()

        val minVersion = IsSettingsService.getInstance().state.minInnoVersion
        specSections.forEach { specSection ->
            val duplicate = specSection.name.lowercase() in existingNames
            val since = specSection.since
            val until = specSection.until
            val tooNew = minVersion != null && since != null &&
                    IsSettingsService.compareIsVersions(since, minVersion) > 0
            val removed = minVersion != null && until != null &&
                    IsSettingsService.compareIsVersions(until, minVersion) <= 0
            val tailText = buildString {
                // Deprecation is shown via strikethrough (withStrikeoutness), not as tail text.
                if (removed) append(" [removed IS ${specSection.until}]")
                else if (tooNew) append(" [IS ${specSection.since}+]")
            }
            val element = LookupElementBuilder
                .create(specSection.name)
                .withTypeText(specSection.type.typeName)
                .withTailText(tailText, true)
                .withStrikeoutness(specSection.deprecated.appliesTo(target))
                .withItemTextForeground(
                    when {
                        duplicate -> JBColor.RED
                        removed -> JBColor.GRAY
                        tooNew -> JBColor.ORANGE
                        else -> JBColor.foreground()
                    }
                )
                .withInsertHandler { ctx, _ ->
                    val tail = ctx.tailOffset
                    val chars = ctx.document.charsSequence
                    // If ] is already there (e.g. auto-paired by the IDE), skip past it.
                    if (tail < chars.length && chars[tail] == ']') {
                        ctx.document.insertString(tail + 1, "\n")
                        ctx.editor.caretModel.moveToOffset(tail + 2)
                    } else {
                        ctx.document.insertString(tail, "]\n")
                        ctx.editor.caretModel.moveToOffset(tail + 2)
                    }
                }
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    element,
                    when {
                        duplicate -> -10.0
                        removed -> -20.0
                        tooNew -> -5.0
                        else -> 0.0
                    }
                )
            )
        }
    }
}
