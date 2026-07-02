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

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsSectionSpecTarget
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.appliesTo
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.issFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.languageId
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionDefSpec
import javax.swing.Icon

/**
 * Key completion for internationalized sections (\[Messages], \[CustomMessages], driven by the
 * spec's `internationalization` flag). Offers, at a key position:
 *  • a language-prefix list (flag icon + language name) that inserts `lang.` and re-opens the
 *    popup, and
 *  • the section's known message identifiers (empty for \[CustomMessages]) that insert `name=`.
 * When a `lang.` prefix is already typed, only the message identifiers are offered, matched
 * against the text after the dot.
 */
object IsSectionMessagesKeyProvider : CompletionProvider<CompletionParameters>() {
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

        val inKeyPosition = position.parent is IsSectionDirectiveKey
                || (position.containingParameterEntry == null
                && position.containingDirectiveEntry == null)
        if (!inKeyPosition) return

        val originalFile = parameters.originalFile as? IsScriptFile
        val psiSection = position.containingSection
            ?: parameters.originalPosition?.containingSection
            ?: originalFile?.sectionAtOffset(parameters.offset)
            ?: return
        val specSection = service<IsSpecService>().spec.sections.firstOrNull {
            it.name.equals(psiSection.nameText, ignoreCase = true)
        } ?: return
        if (!specSection.internationalization) return

        val file = originalFile ?: position.issFile ?: return

        // Typed text of the key so far (strip the dummy completion identifier).
        val raw = position.text
        val dummyIdx = raw.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
        val typed = if (dummyIdx >= 0) raw.substring(0, dummyIdx) else raw
        val dotIdx = typed.indexOf('.')

        if (dotIdx >= 0) {
            // A language prefix is already present — only complete message identifiers,
            // matched against the part after the dot.
            val afterDot = result.withPrefixMatcher(typed.substring(dotIdx + 1))
            addMessageIdentifiers(specSection, afterDot, file.specTarget)
            return
        }

        // No dot yet: offer language prefixes (flag + name) and message identifiers.
        languagePrefixSources(file).forEach { (name, displayName, icon) ->
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name)
                        .withTypeText(displayName)
                        .withIcon(icon)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, ".")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                            AutoPopupController.getInstance(ctx.project).scheduleAutoPopup(ctx.editor)
                        },
                    20.0
                )
            )
        }
        addMessageIdentifiers(specSection, result, file.specTarget)
    }

    private fun addMessageIdentifiers(
        specSection: IsSectionDefSpec,
        result: CompletionResultSet,
        target: IsSectionSpecTarget
    ) {
        specSection.attributes.forEach { attr ->
            // Deprecation is shown via strikethrough (withStrikeoutness) + gray colour, not as tail text.
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(attr.name)
                        .withTypeText("message")
                        .withStrikeoutness(attr.deprecated.appliesTo(target))
                        .withItemTextForeground(
                            if (attr.deprecated.appliesTo(target)) JBColor.GRAY else JBColor.foreground()
                        )
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, "=")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        },
                    0.0
                )
            )
        }
    }

    /**
     * Languages offered as a key prefix: the `Name` values declared in the file's \[Languages]
     * section (with the matching flag + display name), falling back to all built-in languages
     * when the file declares none.
     */
    /**
     * Prefix sources are the `Name` values declared in the file's \[Languages] section; flag and
     * English name are derived from each entry's MessagesFile → LanguageID (the single source of
     * truth). No \[Languages] section ⇒ no prefix suggestions.
     */
    private fun languagePrefixSources(file: IsScriptFile): List<Triple<String, String, Icon>> =
        file.findSections("Languages")
            .flatMap { it.nameDeclarations }
            .mapNotNull { pair ->
                val name = pair.valueUnquoted.ifEmpty { null } ?: return@mapNotNull null
                val messagesFile = pair.containingParameterEntry?.paramPairList
                    ?.firstOrNull { it.keyText().equals("MessagesFile", ignoreCase = true) }
                    ?.valueUnquoted
                val lang = messagesFile
                    ?.let { file.languageId(it) }
                    ?.let { service<IsLanguageDataService>().fromId(it) }
                Triple(name, lang?.displayName ?: name, lang?.icon ?: AllIcons.General.Web)
            }
            .distinctBy { it.first.lowercase() }
}
