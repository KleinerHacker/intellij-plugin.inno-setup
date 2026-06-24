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

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent

/**
 * Project-specific settings sub-page ("Inno Setup | Build") for how `.iss` scripts are compiled on
 * project build. Stored per project in [IsBuildSettingsService]; registered as a project configurable
 * so the project scope marker stays on this node only, not on the IDE-wide installation page.
 */
class IsBuildSettingsConfigurable(private val project: Project) : SearchableConfigurable {

    private val state get() = IsBuildSettingsService.getInstance(project).state

    private var compileOnBuildCheck: JBCheckBox? = null
    private var outputModeCombo: ComboBox<IsBuildOutputMode>? = null

    /**
     * Returns the stable settings page id used by IntelliJ search.
     */
    override fun getId() = "org.pcsoft.intellij.plugin.inno_setup.settings.build"

    /**
     * Returns the display name shown in the Settings tree.
     */
    override fun getDisplayName() = PluginBundle.message("settings.build.display_name")

    /**
     * Builds the Swing component for the build settings sub-page.
     */
    override fun createComponent(): JComponent {
        val compileCheck = JBCheckBox("Compile .iss files on project build")
        compileOnBuildCheck = compileCheck
        val outputCombo = ComboBox(DefaultComboBoxModel(IsBuildOutputMode.entries.toTypedArray()))
        outputCombo.renderer = SimpleListCellRenderer.create { label, value, _ -> label.text = value?.label ?: "" }
        outputModeCombo = outputCombo

        // The output mode only matters when compilation on build is enabled.
        compileCheck.addItemListener { outputCombo.isEnabled = compileCheck.isSelected }
        outputCombo.isEnabled = compileCheck.isSelected

        return panel {
            group("Build") {
                row { cell(compileCheck) }
                row("Output:") { cell(outputCombo) }
                row {
                    comment(
                        "When building the project, all top-level <code>.iss</code> scripts are compiled " +
                                "with ISCC. <b>Into project build directory</b> maps a relative " +
                                "<code>OutputDir</code> below the project's build folder " +
                                "(<code>out</code>/<code>target</code>/<code>build</code>). " +
                                "These options are stored per project."
                    )
                }
            }
        }
    }

    /**
     * Checks whether the current UI values differ from the persisted project settings.
     */
    override fun isModified(): Boolean {
        if (compileOnBuildCheck?.isSelected != state.compileOnBuild) return true
        return selectedOutputMode().name != state.outputMode
    }

    /**
     * Stores the current UI values in the project settings state.
     */
    override fun apply() {
        state.compileOnBuild = compileOnBuildCheck?.isSelected ?: true
        state.outputMode = selectedOutputMode().name
    }

    /**
     * Restores UI values from the project settings state.
     */
    override fun reset() {
        compileOnBuildCheck?.isSelected = state.compileOnBuild
        outputModeCombo?.let { combo ->
            combo.selectedItem = IsBuildOutputMode.fromName(state.outputMode)
            combo.isEnabled = state.compileOnBuild
        }
    }

    /**
     * Releases Swing component references owned by this configurable.
     */
    override fun disposeUIResources() {
        compileOnBuildCheck = null
        outputModeCombo = null
    }

    private fun selectedOutputMode(): IsBuildOutputMode =
        outputModeCombo?.selectedItem as? IsBuildOutputMode ?: IsBuildOutputMode.DEFAULT
}
