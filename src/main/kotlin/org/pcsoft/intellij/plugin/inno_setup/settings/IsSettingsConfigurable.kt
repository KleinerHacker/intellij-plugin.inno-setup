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
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * IDE-wide settings page for the Inno Setup installation and version validation. These are
 * application-level (shared across all projects); the per-project build behaviour lives on the
 * separate [IsBuildSettingsConfigurable] sub-page.
 */
class IsSettingsConfigurable : SearchableConfigurable {

    /**
     * Version-list helpers used by the settings page and unit tests.
     */
    companion object {
        /** Stable id; used as the parent for the project-level build sub-page. */
        const val ID = "org.pcsoft.intellij.plugin.inno_setup.settings"

        private val VERSIONS_BY_MAJOR = mapOf(
            6 to listOf("6.0", "6.1", "6.2", "6.3", "6.4", "6.5", "6.6", "6.7"),
            7 to listOf("7.0")
        )
        private val ALL_VERSIONS = VERSIONS_BY_MAJOR.values.flatten()
        private const val DEFAULT_LATEST = "6.7"

        /**
         * Returns the selectable minor versions for a detected major version.
         */
        fun versionsForMajor(major: Int?): List<String> =
            if (major != null) VERSIONS_BY_MAJOR[major] ?: ALL_VERSIONS
            else ALL_VERSIONS

        /**
         * Returns the latest known minor version for a detected major version.
         */
        fun latestForMajor(major: Int?): String =
            VERSIONS_BY_MAJOR[major]?.last() ?: DEFAULT_LATEST

        /**
         * Extracts the major version number from the text returned by the installation detector.
         */
        fun extractMajorFromDetected(detectedVersion: String?): Int? =
            detectedVersion?.let { Regex("\\d+").find(it)?.value?.toIntOrNull() }

        /**
         * Extracts the major version number from a saved minimum-version restriction.
         */
        fun extractMajorFromRestriction(restriction: String?): Int? =
            restriction?.let { Regex("^(\\d+)\\.").find(it)?.groupValues?.get(1)?.toIntOrNull() }
    }

    private val service get() = IsSettingsService.getInstance()

    private var pathField: TextFieldWithBrowseButton? = null
    private var versionLabel: JBLabel? = null
    private var validationLabel: JBLabel? = null
    private var minVersionCombo: ComboBox<String>? = null

    /**
     * Returns the stable settings page id used by IntelliJ search.
     */
    override fun getId() = ID

    /**
     * Returns the display name shown in the Settings tree.
     */
    override fun getDisplayName() = "Inno Setup"

    /**
     * Builds the Swing component for the settings page.
     */
    override fun createComponent(): JComponent {
        val picker = TextFieldWithBrowseButton()
        pathField = picker
        versionLabel = JBLabel("–")
        validationLabel = JBLabel("")
        val combo = ComboBox<String>()
        minVersionCombo = combo

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
            group("Version Validation") {
                row("Minimum Inno Setup version:") { cell(combo) }
                row {
                    comment(
                        "Limits available version options to the detected major version. " +
                                "Used for version-aware warnings in <code>.iss</code> files."
                    )
                }
            }
        }
    }

    /**
     * Checks whether the current UI values differ from the persisted settings.
     */
    override fun isModified(): Boolean {
        // Treat a null/empty installation path equivalently: BaseState.string() collapses "" to null,
        // so a freshly cleared field would otherwise always read as modified.
        if ((pathField?.text?.trim() ?: "") != (service.state.installationPath ?: "")) return true
        return minVersionCombo?.selectedItem?.toString() != service.state.minInnoVersion
    }

    /**
     * Stores the current UI values in the persistent settings state.
     */
    override fun apply() {
        service.state.installationPath = pathField?.text?.trim() ?: ""
        service.state.minInnoVersion = minVersionCombo?.selectedItem?.toString()
    }

    /**
     * Restores UI values from the persistent settings state.
     */
    override fun reset() {
        val path = service.state.installationPath ?: ""
        pathField?.text = path
        // updateLabels fires via the document listener above; it rebuilds the combo
        // for the detected major and selects the latest minor.
        // Then we restore the saved selection if it is still valid for that major.
        val savedVersion = service.state.minInnoVersion
        if (savedVersion != null) {
            val currentItems = (0 until (minVersionCombo?.itemCount ?: 0))
                .map { minVersionCombo?.getItemAt(it) }
            if (savedVersion in currentItems) {
                minVersionCombo?.selectedItem = savedVersion
            }
        }
    }

    /**
     * Releases Swing component references owned by this configurable.
     */
    override fun disposeUIResources() {
        pathField = null
        versionLabel = null
        validationLabel = null
        minVersionCombo = null
    }

    private fun updateLabels(path: String) {
        val detected = service.detectVersion(path)
        versionLabel?.text = detected ?: "–"

        val detectedMajor = extractMajorFromDetected(detected)
        val newVersions = versionsForMajor(detectedMajor)

        minVersionCombo?.let { combo ->
            val currentItems = (0 until combo.itemCount).map { combo.getItemAt(it) }
            if (currentItems != newVersions) {
                // Major changed (or first population) — rebuild and default to latest minor
                val prevSelection = combo.selectedItem?.toString()
                val prevMajor = extractMajorFromRestriction(prevSelection)
                combo.model = DefaultComboBoxModel(newVersions.toTypedArray())
                // Keep selection only when the major version matches; otherwise pick latest
                combo.selectedItem =
                    if (prevMajor == detectedMajor && prevSelection in newVersions) prevSelection
                    else latestForMajor(detectedMajor)
            }
            // If items didn't change (same major), leave the current selection untouched
        }

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
