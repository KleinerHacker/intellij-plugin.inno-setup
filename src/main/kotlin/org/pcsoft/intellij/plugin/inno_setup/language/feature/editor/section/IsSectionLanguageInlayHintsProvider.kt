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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IconUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.languageId
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamPair
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService
import javax.swing.Icon
import javax.swing.JPanel

/**
 * Renders flag inlay hints, all derived from the Windows language id (the single source of truth):
 *  - \[LangOptions] `LanguageID` &mdash; flag + locale name after `=`, before the numeric id.
 *  - \[Languages] `MessagesFile` &mdash; flag + locale name of the language declared in the
 *    referenced `.isl` file (`LangOptions.LanguageID`).
 *  - \[Messages] / \[CustomMessages] &mdash; the flag of the language a `lang.` key prefix refers
 *    to, before the key.
 *
 * When the value does not map to a known locale, no hint is shown.
 */
@Suppress("UnstableApiUsage")
class IsSectionLanguageInlayHintsProvider : InlayHintsProvider<NoSettings> {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override val key: SettingsKey<NoSettings> = SettingsKey("inno.iss.language.flags")
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override val name: String = "Language flags"
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override val previewText: String =
        "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n\n[LangOptions]\nLanguageID=\$0409"

    /**
     * Returns inlay-hint configuration or collection support.
     */
    override fun createSettings(): NoSettings = NoSettings()

    /**
     * Returns inlay-hint configuration or collection support.
     */
    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable =
        object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JPanel = JPanel()
        }

    /**
     * Returns inlay-hint configuration or collection support.
     */
    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector = object : FactoryInlayHintsCollector(editor) {

        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            when (element) {
                is IsSectionDirectiveEntry -> {
                    collectLanguageId(element, sink)
                    collectMessagesPrefixFlag(element, sink)
                }

                is IsSectionParamPair -> collectLanguagesFlag(element, sink)
            }
            return true
        }

        // \[LangOptions] LanguageID=$0409 → 🇺🇸 English (United States)
        private fun collectLanguageId(entry: IsSectionDirectiveEntry, sink: InlayHintsSink) {
            if (entry.isInCodeSection) return
            if (!entry.keyText().equals("LanguageID", ignoreCase = true)) return
            if (entry.containingSection?.nameText?.equals("LangOptions", ignoreCase = true) != true) return

            val value = entry.paramValue ?: return
            val numeric = IsLanguageDataService.parseId(value.singleText.trim()) ?: return
            val lang = service<IsLanguageDataService>().fromId(numeric) ?: return

            addFlag(sink, value.textRange.startOffset, lang.icon, lang.displayName)
        }

        // \[Languages] MessagesFile: "compiler:Languages\German.isl" → 🇩🇪 German (Germany)
        // Flag + English name come from the LanguageID declared in the referenced .isl file.
        private fun collectLanguagesFlag(pair: IsSectionParamPair, sink: InlayHintsSink) {
            if (pair.isInCodeSection) return
            if (pair.containingSection?.nameText?.equals("Languages", ignoreCase = true) != true) return
            if (!pair.keyText().equals("MessagesFile", ignoreCase = true)) return

            val value = pair.paramValue ?: return
            val text = value.singleText.trim()
            if (text.isEmpty()) return

            val ctx = pair.containingFile as? IsScriptFile ?: return
            val lcid = ctx.languageId(text) ?: return
            val lang = service<IsLanguageDataService>().fromId(lcid) ?: return

            addFlag(sink, value.textRange.startOffset, lang.icon, lang.displayName)
        }

        // \[Messages]/\[CustomMessages] english.WelcomeLabel1=… → 🇺🇸 before the key.
        // The flag is that of the language the "lang." prefix refers to in \[Languages].
        private fun collectMessagesPrefixFlag(entry: IsSectionDirectiveEntry, sink: InlayHintsSink) {
            if (entry.isInCodeSection) return

            val section = entry.containingSection ?: return
            if (section.specSection?.internationalization != true) return

            val key = entry.keyText()
            val dot = key.indexOf('.')
            if (dot <= 0) return

            val ctx = entry.containingFile as? IsScriptFile ?: return
            val icon = languageFlagForPrefix(ctx, key.substring(0, dot)) ?: return

            addFlag(sink, entry.directiveKey.textRange.startOffset, icon, null)
        }

        /** Flag of the \[Languages] entry whose Name equals [prefix], via its MessagesFile LanguageID. */
        private fun languageFlagForPrefix(file: IsScriptFile, prefix: String): Icon? {
            val namePair = file.findSections("Languages").flatMap { it.nameDeclarations }
                .firstOrNull { it.valueUnquoted.equals(prefix, ignoreCase = true) } ?: return null
            val messagesFile = namePair.containingParameterEntry?.paramPairList
                ?.firstOrNull { it.keyText().equals("MessagesFile", ignoreCase = true) }
                ?.valueUnquoted ?: return null
            val lcid = file.languageId(messagesFile) ?: return null

            return service<IsLanguageDataService>().fromId(lcid)?.icon
        }

        private fun addFlag(sink: InlayHintsSink, offset: Int, icon: Icon, label: String?) {
            val presentation: InlayPresentation =
                if (label != null)
                // LanguageID: leading space after '=', then flag + locale name.
                    factory.seq(factory.smallText(" "), centeredFlag(icon), centeredText(" $label "))
                else
                    factory.seq(centeredFlag(icon), factory.smallText(" "))
            sink.addInlineElement(offset, true, presentation, false)
        }

        /**
         * Flags are authored at 16×16; render them scaled down and nudged downward so they sit
         * vertically centred on the line instead of aligning to its top.
         */
        private fun centeredFlag(icon: Icon): InlayPresentation {
            val scaled = IconUtil.scale(icon, null, 0.7f)
            val top = ((editor.lineHeight - scaled.iconHeight) / 2).coerceAtLeast(0)

            return factory.inset(factory.icon(scaled), top = top)
        }

        private fun centeredText(text: String): InlayPresentation {
            val top = ((editor.lineHeight - factory.smallText(text).height) / 2).coerceAtLeast(0)

            return factory.inset(factory.smallText(text), top = top)
        }
    }
}
