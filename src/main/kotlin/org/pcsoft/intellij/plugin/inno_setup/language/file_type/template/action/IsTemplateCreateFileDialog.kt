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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.action

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import java.awt.Dimension
import javax.swing.JComponent

/**
 * Minimal dialog for the "New Inno Setup Template" action: a `.ist` file is free text, so only the file
 * name is requested.
 */
class IsTemplateCreateFileDialog(project: Project) : DialogWrapper(project) {

    private val fileNameField = JBTextField()

    val fileName: String get() = fileNameField.text.trim()

    init {
        title = PluginBundle.message("dialog.ist.new_file.title")
        init()
    }

    override fun createCenterPanel(): JComponent = panel {
        row(PluginBundle.message("dialog.ist.new_file.prompt")) {
            cell(fileNameField).focused().align(Align.FILL)
        }
    }.also { it.minimumSize = Dimension(400, 60) }

    override fun doValidate(): ValidationInfo? = when {
        fileName.isEmpty() -> ValidationInfo(PluginBundle.message("dialog.ist.new_file.empty"), fileNameField)
        else -> null
    }

    override fun getPreferredFocusedComponent() = fileNameField
}
