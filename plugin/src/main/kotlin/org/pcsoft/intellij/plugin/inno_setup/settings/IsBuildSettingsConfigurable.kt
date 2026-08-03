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

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.listCellRenderer
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.ListCellRenderer

/**
 * Project-specific settings sub-page ("Inno Setup | Build") for how `.iss` scripts are compiled on
 * project build, and for the project's named build configurations. Stored per project in
 * [IsBuildSettingsService] and [IsBuildConfigurationService]; registered as a project configurable
 * so the project scope marker stays on this node only, not on the IDE-wide installation page.
 */
class IsBuildSettingsConfigurable(private val project: Project) : SearchableConfigurable {

    private val state get() = IsBuildSettingsService.getInstance(project).state
    private val configService get() = IsBuildConfigurationService.getInstance(project)

    private var compileOnBuildCheck: JBCheckBox? = null
    private var outputModeCombo: ComboBox<IsBuildOutputMode>? = null

    // ── Build configurations ──────────────────────────────────────────────────────────────────────
    // The page edits a detached copy; nothing reaches the `.build` directory before apply(), so a
    // cancelled dialog leaves the files exactly as they were.
    private val working = mutableListOf<IsBuildConfiguration>()
    private var selectedIndex = -1

    /** Guards the combo listener while the UI is repopulated programmatically. */
    private var loading = false

    private var configCombo: ComboBox<String>? = null
    private var symbolsField: JBTextField? = null
    private var outputDirField: TextFieldWithBrowseButton? = null
    private var optionsField: JBTextField? = null
    private var removeButton: JButton? = null

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
        // listCellRenderer(defaultText, …) yields a ListCellRenderer<T?>; cast to the combo's non-null
        // element type so the assignment matches the JComboBox.setRenderer(ListCellRenderer<? super E>) bound.
        @Suppress("UNCHECKED_CAST")
        outputCombo.renderer = listCellRenderer<IsBuildOutputMode>("") { text(value.label) } as ListCellRenderer<IsBuildOutputMode>
        outputModeCombo = outputCombo

        // The output mode only matters when compilation on build is enabled.
        compileCheck.addItemListener { outputCombo.isEnabled = compileCheck.isSelected }
        outputCombo.isEnabled = compileCheck.isSelected

        val combo = ComboBox<String>().apply {
            @Suppress("UNCHECKED_CAST")
            renderer = listCellRenderer<String>("") {
                icon(AllIcons.General.GearPlain)
                text(value)
            } as ListCellRenderer<String>
            addActionListener { if (!loading) onConfigurationSelected(selectedIndex()) }
        }
        configCombo = combo

        val symbols = JBTextField().apply {
            toolTipText = PluginBundle.message("settings.build.config.symbols.tooltip")
        }
        symbolsField = symbols
        val outputDir = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                project,
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
                    .withTitle(PluginBundle.message("settings.build.config.output_dir.chooser"))
            )
        }
        outputDirField = outputDir
        val options = JBTextField().apply {
            toolTipText = PluginBundle.message("settings.build.config.options.tooltip")
        }
        optionsField = options

        val addButton = JButton(PluginBundle.message("settings.build.config.add")).apply {
            addActionListener { addConfiguration() }
        }
        val copyButton = JButton(PluginBundle.message("settings.build.config.copy")).apply {
            addActionListener { copyConfiguration() }
        }
        val renameButton = JButton(PluginBundle.message("settings.build.config.rename")).apply {
            addActionListener { renameConfiguration() }
        }
        val deleteButton = JButton(PluginBundle.message("settings.build.config.remove")).apply {
            addActionListener { removeConfiguration() }
        }
        removeButton = deleteButton

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
            group(PluginBundle.message("settings.build.config.group")) {
                row(PluginBundle.message("settings.build.config.configuration") + ":") {
                    cell(combo).align(Align.FILL)
                }
                row {
                    cell(addButton)
                    cell(copyButton)
                    cell(renameButton)
                    cell(deleteButton)
                }
                row(PluginBundle.message("settings.build.config.symbols") + ":") {
                    cell(symbols).align(Align.FILL)
                }
                row(PluginBundle.message("settings.build.config.output_dir") + ":") {
                    cell(outputDir).align(Align.FILL)
                }
                row(PluginBundle.message("settings.build.config.options") + ":") {
                    cell(options).align(Align.FILL)
                }
                row { comment(PluginBundle.message("settings.build.config.comment")) }
            }
        }
    }

    /**
     * Checks whether the current UI values differ from the persisted project settings.
     */
    override fun isModified(): Boolean {
        if (compileOnBuildCheck?.isSelected != state.compileOnBuild) return true
        if (selectedOutputMode().name != state.outputMode) return true
        flushDetails()
        return working != configService.all()
    }

    /**
     * Stores the current UI values in the project settings state and writes the build configuration files.
     */
    override fun apply() {
        flushDetails()
        validateConfigurations()
        state.compileOnBuild = compileOnBuildCheck?.isSelected ?: true
        state.outputMode = selectedOutputMode().name
        configService.replaceAll(working.toList())
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

        configService.ensureDefaults()
        working.clear()
        working += configService.all()
        selectedIndex = if (working.isEmpty()) -1 else 0
        reloadCombo()
        loadDetails()
    }

    /**
     * Releases Swing component references owned by this configurable.
     */
    override fun disposeUIResources() {
        compileOnBuildCheck = null
        outputModeCombo = null
        configCombo = null
        symbolsField = null
        outputDirField = null
        optionsField = null
        removeButton = null
    }

    private fun selectedOutputMode(): IsBuildOutputMode =
        outputModeCombo?.selectedItem as? IsBuildOutputMode ?: IsBuildOutputMode.DEFAULT

    // ── Build configuration editing ───────────────────────────────────────────────────────────────

    private fun selectedIndex(): Int = configCombo?.selectedIndex ?: -1

    /** Writes the detail fields back into the configuration they belong to. */
    private fun flushDetails() {
        val index = selectedIndex
        if (index !in working.indices) return
        working[index] = working[index].copy(
            compilerSymbols = symbolsField?.text?.trim().orEmpty(),
            outputDirOverride = outputDirField?.text?.trim().orEmpty(),
            additionalIsccOptions = optionsField?.text?.trim().orEmpty()
        )
    }

    private fun loadDetails() {
        val config = working.getOrNull(selectedIndex)
        symbolsField?.text = config?.compilerSymbols.orEmpty()
        outputDirField?.text = config?.outputDirOverride.orEmpty()
        optionsField?.text = config?.additionalIsccOptions.orEmpty()
        val editable = config != null
        symbolsField?.isEnabled = editable
        outputDirField?.isEnabled = editable
        optionsField?.isEnabled = editable
        // The last configuration must stay: a project without one cannot run anything.
        removeButton?.isEnabled = working.size > 1
    }

    private fun onConfigurationSelected(newIndex: Int) {
        if (newIndex == selectedIndex || newIndex !in working.indices) return
        flushDetails()
        selectedIndex = newIndex
        loadDetails()
    }

    private fun reloadCombo() {
        val combo = configCombo ?: return
        loading = true
        try {
            combo.removeAllItems()
            working.forEach { combo.addItem(it.name) }
            if (selectedIndex in working.indices) combo.selectedIndex = selectedIndex
        } finally {
            loading = false
        }
    }

    private fun addConfiguration() {
        val name = askName(PluginBundle.message("settings.build.config.add.title"), "") ?: return
        flushDetails()
        working += IsBuildConfiguration(name)
        selectedIndex = working.lastIndex
        reloadCombo()
        loadDetails()
    }

    private fun copyConfiguration() {
        flushDetails()
        val source = working.getOrNull(selectedIndex) ?: return
        val name = askName(PluginBundle.message("settings.build.config.copy.title"), source.name + " (2)") ?: return
        working += source.copy(name = name)
        selectedIndex = working.lastIndex
        reloadCombo()
        loadDetails()
    }

    private fun renameConfiguration() {
        flushDetails()
        val source = working.getOrNull(selectedIndex) ?: return
        val name = askName(PluginBundle.message("settings.build.config.rename.title"), source.name) ?: return
        working[selectedIndex] = source.copy(name = name)
        reloadCombo()
    }

    private fun removeConfiguration() {
        if (working.size <= 1) return
        val index = selectedIndex
        if (index !in working.indices) return
        working.removeAt(index)
        selectedIndex = index.coerceAtMost(working.lastIndex)
        reloadCombo()
        loadDetails()
    }

    /**
     * Asks for a configuration name, rejecting blanks and duplicates on the spot — a duplicate would share
     * a file with the configuration it collides with and silently overwrite it.
     */
    private fun askName(title: String, initial: String): String? {
        val taken = working.mapIndexed { i, c -> if (i == selectedIndex) null else c.name }.filterNotNull().toSet()
        val input = Messages.showInputDialog(
            project,
            PluginBundle.message("settings.build.config.name_prompt"),
            title,
            AllIcons.General.GearPlain,
            initial,
            object : InputValidator {
                override fun checkInput(inputString: String?): Boolean =
                    !inputString.isNullOrBlank() && inputString.trim() !in taken

                override fun canClose(inputString: String?): Boolean = checkInput(inputString)
            }
        )
        return input?.trim()?.takeIf { it.isNotEmpty() }
    }

    /** Rejects a state the `.build` directory could not represent unambiguously. */
    private fun validateConfigurations() {
        if (working.isEmpty())
            throw ConfigurationException(PluginBundle.message("settings.build.config.error.empty"))
        val duplicate = working.groupBy { it.name }.entries.firstOrNull { it.value.size > 1 }
        if (duplicate != null)
            throw ConfigurationException(
                PluginBundle.message("settings.build.config.error.duplicate", duplicate.key)
            )
    }
}
