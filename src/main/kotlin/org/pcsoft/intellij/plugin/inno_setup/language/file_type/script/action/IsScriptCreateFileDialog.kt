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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.action

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.CheckBoxList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.types.IsLanguageDataSpec
import java.awt.Dimension
import javax.swing.JComponent

/**
 * Collects user input required to create a new Inno Setup file.
 */
class IsScriptCreateFileDialog(project: Project) : DialogWrapper(project) {

    private val fileNameField = JBTextField()
    private val appNameField = JBTextField()
    private val appVersionField = JBTextField("1.0")

    private val languages = service<IsLanguageDataService>().builtinLanguages
    private val languageList = CheckBoxList<IsLanguageDataSpec>().apply {
        languages.forEach { lang ->
            addItem(lang, lang.displayName, lang.issName.equals("english", ignoreCase = true))
        }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val fileName: String get() = fileNameField.text.trim()
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val appName: String get() = appNameField.text.trim()
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val appVersion: String get() = appVersionField.text.trim()
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val selectedLanguages: List<IsLanguageDataSpec>
        get() = languages.filter { languageList.isItemSelected(it) }

    init {
        title = "New Inno Setup Script"
        init()
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun createCenterPanel(): JComponent = panel {
        row("File name:") { cell(fileNameField).focused().align(Align.FILL) }
        row("App name:") { cell(appNameField).align(Align.FILL) }
        row("App version:") { cell(appVersionField).align(Align.FILL) }
        row("Languages:") {
            cell(JBScrollPane(languageList)).align(Align.FILL)
        }.resizableRow()
    }.also { it.minimumSize = Dimension(500, 300) }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun doValidate(): ValidationInfo? = when {
        fileName.isEmpty() -> ValidationInfo("File name must not be empty", fileNameField)
        appName.isEmpty() -> ValidationInfo("App name must not be empty", appNameField)
        appVersion.isEmpty() -> ValidationInfo("App version must not be empty", appVersionField)
        selectedLanguages.isEmpty() -> ValidationInfo("At least one language must be selected", languageList)
        else -> null
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getPreferredFocusedComponent() = fileNameField
}
