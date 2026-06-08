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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.NoSettings
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IconUtil
import org.pcsoft.intellij.plugin.inno_setup.action.IssScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.action.IssWindowsLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.isi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import javax.swing.Icon
import javax.swing.JPanel

/**
 * Renders flag inlay hints:
 *  - `[LangOptions]` `LanguageID` &mdash; flag + locale name after `=`, before the numeric id.
 *  - `[Languages]` `Name` / `MessagesFile` &mdash; the built-in language flag before the string.
 *
 * When the value does not map to a known language/locale, no hint is shown.
 */
@Suppress("UnstableApiUsage")
class IsiLanguageInlayHintsProvider : InlayHintsProvider<NoSettings> {

    override val key: SettingsKey<NoSettings> = SettingsKey("inno.iss.language.flags")
    override val name: String = "Language flags"
    override val previewText: String =
        "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n\n[LangOptions]\nLanguageID=\$0409"

    override fun createSettings(): NoSettings = NoSettings()

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable =
        object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JPanel = JPanel()
        }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector = object : FactoryInlayHintsCollector(editor) {

        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            when (element) {
                is IsiDirectiveEntry -> collectLanguageId(element, sink)
                is IsiParamPair -> collectLanguagesFlag(element, sink)
            }
            return true
        }

        // [LangOptions] LanguageID=$0409 → 🇺🇸 English (United States)
        private fun collectLanguageId(entry: IsiDirectiveEntry, sink: InlayHintsSink) {
            if (entry.isInCodeSection()) return
            if (!entry.keyText().equals("LanguageID", ignoreCase = true)) return
            if (entry.containingSection()?.nameText()?.equals("LangOptions", ignoreCase = true) != true) return

            val value = entry.paramValue ?: return
            val numeric = IssWindowsLanguage.parseId(value.singleText().trim()) ?: return
            val lang = IssWindowsLanguage.fromId(numeric) ?: return
            addFlag(sink, value.textRange.startOffset, lang.icon, lang.displayName)
        }

        // [Languages] Name: "english" / MessagesFile: "compiler:Default.isl" → 🇬🇧 "english"
        private fun collectLanguagesFlag(pair: IsiParamPair, sink: InlayHintsSink) {
            if (pair.isInCodeSection()) return
            if (pair.containingSection()?.nameText()?.equals("Languages", ignoreCase = true) != true) return

            val value = pair.paramValue ?: return
            val text = value.singleText().trim()
            if (text.isEmpty()) return
            val icon = when {
                pair.keyText().equals("Name", ignoreCase = true) -> IssScriptLanguage.fromIssName(text)?.icon
                pair.keyText().equals("MessagesFile", ignoreCase = true) -> IssScriptLanguage.fromMessagesFile(text)?.icon
                else -> null
            } ?: return
            addFlag(sink, value.textRange.startOffset, icon, null)
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
