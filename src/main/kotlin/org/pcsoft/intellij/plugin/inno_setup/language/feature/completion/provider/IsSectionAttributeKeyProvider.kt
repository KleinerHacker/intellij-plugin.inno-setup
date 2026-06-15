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

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamKey
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.*

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionAttributeKeyProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.position.isInCodeSection) return
        val position = parameters.position

        // Only suggest attribute keys in key positions:
        //  • parent is IsSectionParamKey / IsSectionDirectiveKey → user is editing an existing key
        //  • not inside any entry at all → user is on an empty / partial line
        // Anything else (inside a value) is skipped.
        val inKeyPosition = position.parent is IsSectionParamKey
                || position.parent is IsSectionDirectiveKey
                || (position.containingParameterEntry == null
                && position.containingDirectiveEntry == null)
        if (!inKeyPosition) return

        // When typing on an empty line, or after a dangling ';' on a parameter
        // line, the dummy IDENTIFIER lands outside the section (the entry* loop
        // exits before consuming it). Fall back to the element at the same offset
        // in the original file, and finally to an offset-based section lookup,
        // which works even when the trailing tokens are outside any section.
        val originalFile = parameters.originalFile as? IsScriptFile
        val psiSection = position.containingSection
            ?: parameters.originalPosition?.containingSection
            ?: originalFile?.sectionAtOffset(parameters.offset)
            ?: return
        val sectionName = psiSection.nameText

        val specSections = service<IsSpecService>().spec.sections
        val specSection = specSections.firstOrNull {
            it.name.equals(sectionName, ignoreCase = true)
        } ?: return

        // Sections that support a language prefix (e.g. \[Messages], \[CustomMessages]) are
        // handled by MessagesKeyProvider, which also offers the language-prefix list and copes
        // with the embedded "lang." prefix. Skip them here to avoid duplicate suggestions.
        if (specSection.internationalization) return

        // Directive keys are unique per section; parameter keys are unique per
        // line (entry). So for parameter sections, only the keys already present
        // on the current line count as duplicates — not the whole section.
        val usedKeys = if (specSection.type == IsSectionType.DIRECTIVE) {
            psiSection.directiveEntryList.map { it.directiveKey.text.trim().lowercase() }.toSet()
        } else {
            val document = parameters.editor.document
            val entry = position.containingParameterEntry
                ?: parameters.originalPosition?.containingParameterEntry
                ?: psiSection.parameterEntryOnLineOf(parameters.offset, document)
            entry?.paramPairList?.map { it.keyText().lowercase() }?.toSet().orEmpty()
        }

        val minVersion = IsSettingsService.getInstance().state.minInnoVersion
        val target = originalFile?.specTarget ?: IsSectionSpecTarget.ISS
        specSection.attributes.forEach { attr ->
            val duplicate = attr.name.lowercase() in usedKeys
            val tooNew = minVersion != null && attr.since != null &&
                    IsSettingsService.compareIsVersions(attr.since, minVersion) > 0
            val removed = minVersion != null && attr.until != null &&
                    IsSettingsService.compareIsVersions(attr.until, minVersion) <= 0
            val typeHint = when (val t = attr.type) {
                is IsSectionNativeTypeSpec -> t.dataType.typeName
                is IsSectionReferenceTypeSpec -> "→ ${t.section}"
                is IsSectionFlagTypeSpec -> "flags"
            }
            val tail = buildString {
                if (attr.required.appliesTo(target)) append(" required")
                if (attr.deprecated.appliesTo(target)) append(" deprecated")
                if (attr.array) append("[]")
                if (removed) append(" [removed IS ${attr.until}]")
                else if (tooNew) append(" [IS ${attr.since}+]")
            }
            val separator = if (specSection.type == IsSectionType.DIRECTIVE) "=" else ": "
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
                .withBoldness(attr.required.appliesTo(target))
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
