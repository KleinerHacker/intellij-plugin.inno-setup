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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.action

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsLanguageDataSpec
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JList

/**
 * Dialog for the "New Inno Setup Language" action. It asks for the file name, the language name
 * (\[LangOptions]/`LanguageName`) and the language id (\[LangOptions]/`LanguageID`).
 *
 * The language id is picked from a combo box whose entries mirror the look of the `LanguageID`
 * completion popup (see
 * [org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsSectionLanguageIdValueProvider]):
 * flag icon, the locale name, and the greyed hex LCID on the right. Selecting a language pre-fills
 * the (still editable) language-name field.
 */
class IsLanguageCreateFileDialog(project: Project) : DialogWrapper(project) {

    private val fileNameField = JBTextField()
    private val languageNameField = JBTextField()

    private val languages = service<IsLanguageDataService>().entries
    private val languageIdCombo = ComboBox(languages.toTypedArray()).apply {
        renderer = object : ColoredListCellRenderer<IsLanguageDataSpec>() {
            override fun customizeCellRenderer(
                list: JList<out IsLanguageDataSpec>,
                value: IsLanguageDataSpec?,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                value ?: return
                icon = value.icon
                append(value.displayName)
                append("  ${value.id}", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
        selectedItem = languages.firstOrNull { it.issName.equals("english", ignoreCase = true) }
            ?: languages.firstOrNull()
        // Pre-fill the language name with the selected locale; remains user-editable.
        addActionListener { (selectedItem as? IsLanguageDataSpec)?.let { languageNameField.text = it.displayName } }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val fileName: String get() = fileNameField.text.trim()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val languageName: String get() = languageNameField.text.trim()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val languageId: String get() = (languageIdCombo.selectedItem as? IsLanguageDataSpec)?.id.orEmpty()

    init {
        title = "New Inno Setup Language File"
        init()
        (languageIdCombo.selectedItem as? IsLanguageDataSpec)?.let { languageNameField.text = it.displayName }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun createCenterPanel(): JComponent = panel {
        row("File name:") { cell(fileNameField).focused().align(Align.FILL) }
        row("Language name:") { cell(languageNameField).align(Align.FILL) }
        row("Language ID:") { cell(languageIdCombo).align(Align.FILL) }
    }.also { it.minimumSize = Dimension(500, 150) }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun doValidate(): ValidationInfo? = when {
        fileName.isEmpty() -> ValidationInfo("File name must not be empty", fileNameField)
        languageName.isEmpty() -> ValidationInfo("Language name must not be empty", languageNameField)
        languageId.isEmpty() -> ValidationInfo("A language ID must be selected", languageIdCombo)
        else -> null
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getPreferredFocusedComponent() = fileNameField
}
