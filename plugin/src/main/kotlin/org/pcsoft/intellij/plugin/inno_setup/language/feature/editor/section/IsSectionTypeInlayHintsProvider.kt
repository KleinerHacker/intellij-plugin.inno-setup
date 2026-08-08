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
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IconUtil
import org.pcsoft.intellij.plugin.inno_setup.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.specSection
import javax.swing.Icon
import javax.swing.JPanel

/**
 * Renders the icon of a section's entry syntax directly behind the `[` of its header, so the kind of a
 * section is visible while reading the script instead of only while completing its name.
 *
 * The icon is the same one the section-name completion offers: `=` for a directive section such as
 * \[Setup], `:` for a parameter section such as \[Files], and the script icon for \[Code]. A section the
 * specification does not describe gets no hint — an unknown section has no known entry syntax to announce.
 */
@Suppress("UnstableApiUsage")
class IsSectionTypeInlayHintsProvider : InlayHintsProvider<NoSettings> {

    /**
     * Identifies the hint group in the IDE's inlay-hint settings.
     */
    override val key: SettingsKey<NoSettings> = SettingsKey("inno.iss.section.type")

    /**
     * The name of the hint group shown in the IDE's inlay-hint settings.
     */
    override val name: String = "Section type"

    /**
     * The sample script rendered in the settings preview.
     */
    override val previewText: String = "[Setup]\nAppName=My Program\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\""

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
            if (element is IsSectionBlock) collectSectionType(element, sink)
            return true
        }

        // [Setup] → [= Setup], [Files] → [: Files], [Code] → [<script icon> Code]
        private fun collectSectionType(section: IsSectionBlock, sink: InlayHintsSink) {
            val icon = IsIcons.forSectionType(section.specSection?.type) ?: return
            val bracket = section.header.node.findChildByType(IsSectionTypes.LBRACKET) ?: return

            sink.addInlineElement(bracket.textRange.endOffset, false, iconPresentation(icon), false)
        }

        /**
         * Icons are authored at 16×16; render them scaled down and nudged downward so they sit vertically
         * centred on the line instead of aligning to its top, followed by a small gap to the section name.
         */
        private fun iconPresentation(icon: Icon): InlayPresentation {
            val scaled = IconUtil.scale(icon, null, 0.7f)
            val top = ((editor.lineHeight - scaled.iconHeight) / 2).coerceAtLeast(0)

            return factory.seq(factory.inset(factory.icon(scaled), top = top), factory.smallText(" "))
        }
    }
}
