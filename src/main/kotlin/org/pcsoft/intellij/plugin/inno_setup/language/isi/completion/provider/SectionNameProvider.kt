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
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.sections
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IssSettingsService

object SectionNameProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val file = parameters.originalFile as? IssFile ?: return
        val specSections = service<IssSpecService>().spec.sections

        val existingNames = file.sections()
            .map { it.nameText().lowercase() }
            .toSet()

        val minVersion = IssSettingsService.getInstance().state.minInnoVersion
        specSections.forEach { specSection ->
            val duplicate = specSection.name.lowercase() in existingNames
            val tooNew = minVersion != null && specSection.since != null &&
                    IssSettingsService.compareIsVersions(specSection.since, minVersion) > 0
            val removed = minVersion != null && specSection.until != null &&
                    IssSettingsService.compareIsVersions(specSection.until, minVersion) <= 0
            val tailText = buildString {
                if (specSection.deprecated) append(" (deprecated)")
                if (removed) append(" [removed IS ${specSection.until}]")
                else if (tooNew) append(" [IS ${specSection.since}+]")
            }
            val element = LookupElementBuilder
                .create(specSection.name)
                .withTypeText(specSection.type)
                .withTailText(tailText, true)
                .withItemTextForeground(when {
                    duplicate -> JBColor.RED
                    removed -> JBColor.GRAY
                    tooNew -> JBColor.ORANGE
                    else -> JBColor.foreground()
                })
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