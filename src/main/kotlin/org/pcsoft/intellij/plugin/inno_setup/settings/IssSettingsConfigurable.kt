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

package org.pcsoft.intellij.plugin.inno_setup.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class IssSettingsConfigurable : SearchableConfigurable {

    private val service get() = IssSettingsService.getInstance()

    private var pathField: TextFieldWithBrowseButton? = null
    private var versionLabel: JBLabel? = null
    private var validationLabel: JBLabel? = null

    override fun getId() = "org.pcsoft.intellij.plugin.inno_setup.settings"
    override fun getDisplayName() = "Inno Setup"

    override fun createComponent(): JComponent {
        val picker = TextFieldWithBrowseButton()
        pathField = picker
        versionLabel = JBLabel("–")
        validationLabel = JBLabel("")

        picker.addBrowseFolderListener(
            null,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select Inno Setup Installation Directory")
                .withDescription("Choose the folder containing ISCC.exe and Compil32.exe")
        )

        picker.textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = refresh()
            override fun removeUpdate(e: DocumentEvent?) = refresh()
            override fun changedUpdate(e: DocumentEvent?) = refresh()
            private fun refresh() = updateLabels(picker.text.trim())
        })

        return panel {
            group("Inno Setup Installation") {
                row("Installation directory:") { cell(picker).align(Align.FILL) }
                row("Detected version:") { cell(versionLabel!!) }
                row { cell(validationLabel!!) }
                row { comment("The directory must contain <b>ISCC.exe</b> and <b>Compil32.exe</b>.") }
            }
        }
    }

    override fun isModified() =
        pathField?.text?.trim() != service.state.installationPath

    override fun apply() {
        service.state.installationPath = pathField?.text?.trim() ?: ""
    }

    override fun reset() {
        val path = service.state.installationPath ?: ""
        pathField?.text = path
        updateLabels(path)
    }

    override fun disposeUIResources() {
        pathField = null
        versionLabel = null
        validationLabel = null
    }

    private fun updateLabels(path: String) {
        val version = service.detectVersion(path)
        versionLabel?.text = version ?: "–"

        val dir = java.io.File(path)
        val missing = buildList {
            if (path.isNotBlank() && !dir.resolve("ISCC.exe").exists()) add("ISCC.exe")
            if (path.isNotBlank() && !dir.resolve("Compil32.exe").exists()) add("Compil32.exe")
        }
        validationLabel?.apply {
            if (missing.isEmpty()) {
                text = ""
                isVisible = false
            } else {
                text = "<html><font color='red'>Missing: ${missing.joinToString(", ")}</font></html>"
                isVisible = true
            }
        }
    }
}
