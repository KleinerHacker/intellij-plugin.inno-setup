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
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.isInCodeSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parameterEntryOnLineOf
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamKey
import org.pcsoft.intellij.plugin.inno_setup.language.isi.sectionAtOffset
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IssSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.IsiFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiNativeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiReferenceTypeSpec

object AttributeKeyProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.position.isInCodeSection()) return
        val position = parameters.position

        // Only suggest attribute keys in key positions:
        //  • parent is IsiParamKey / IsiDirectiveKey → user is editing an existing key
        //  • not inside any entry at all → user is on an empty / partial line
        // Anything else (inside a value) is skipped.
        val inKeyPosition = position.parent is IsiParamKey
                || position.parent is IsiDirectiveKey
                || (position.containingParameterEntry() == null
                && position.containingDirectiveEntry() == null)
        if (!inKeyPosition) return

        // When typing on an empty line, or after a dangling ';' on a parameter
        // line, the dummy IDENTIFIER lands outside the section (the entry* loop
        // exits before consuming it). Fall back to the element at the same offset
        // in the original file, and finally to an offset-based section lookup,
        // which works even when the trailing tokens are outside any section.
        val originalFile = parameters.originalFile as? IssFile
        val psiSection = position.containingSection()
            ?: parameters.originalPosition?.containingSection()
            ?: originalFile?.sectionAtOffset(parameters.offset)
            ?: return
        val sectionName = psiSection.nameText()

        val specSections = service<IssSpecService>().spec.sections
        val specSection = specSections.firstOrNull {
            it.name.equals(sectionName, ignoreCase = true)
        } ?: return

        // Sections that support a language prefix (e.g. [Messages], [CustomMessages]) are
        // handled by MessagesKeyProvider, which also offers the language-prefix list and copes
        // with the embedded "lang." prefix. Skip them here to avoid duplicate suggestions.
        if (specSection.internationalization) return

        // Directive keys are unique per section; parameter keys are unique per
        // line (entry). So for parameter sections, only the keys already present
        // on the current line count as duplicates — not the whole section.
        val usedKeys = if (specSection.type == "directive") {
            psiSection.directiveEntryList.map { it.directiveKey.text.trim().lowercase() }.toSet()
        } else {
            val document = parameters.editor.document
            val entry = position.containingParameterEntry()
                ?: parameters.originalPosition?.containingParameterEntry()
                ?: psiSection.parameterEntryOnLineOf(parameters.offset, document)
            entry?.paramPairList?.map { it.keyText().lowercase() }?.toSet().orEmpty()
        }

        val minVersion = IssSettingsService.getInstance().state.minInnoVersion
        specSection.attributes.forEach { attr ->
            val duplicate = attr.name.lowercase() in usedKeys
            val tooNew = minVersion != null && attr.since != null &&
                    IssSettingsService.compareIsVersions(attr.since, minVersion) > 0
            val removed = minVersion != null && attr.until != null &&
                    IssSettingsService.compareIsVersions(attr.until, minVersion) <= 0
            val typeHint = when (val t = attr.type) {
                is IsiNativeTypeSpec -> t.dataType
                is IsiReferenceTypeSpec -> "→ ${t.section}"
                is IsiFlagTypeSpec -> "flags"
            }
            val tail = buildString {
                if (attr.required) append(" required")
                if (attr.deprecated) append(" deprecated")
                if (attr.array) append("[]")
                if (removed) append(" [removed IS ${attr.until}]")
                else if (tooNew) append(" [IS ${attr.since}+]")
            }
            val separator = if (specSection.type == "directive") "=" else ": "
            val foreground = when {
                duplicate -> JBColor.RED
                removed -> JBColor.GRAY
                tooNew -> JBColor.ORANGE
                else -> JBColor.foreground()
            }

            val element = LookupElementBuilder
                .create(attr.name)
                .withTypeText(typeHint)
                .withTailText(tail, true)
                .withItemTextForeground(foreground)
                .withBoldness(attr.required)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, separator)
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
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