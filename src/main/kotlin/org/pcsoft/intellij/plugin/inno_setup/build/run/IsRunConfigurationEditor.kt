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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.openapi.vfs.LocalFileSystem
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.valueUnquoted
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.*

/**
 * Settings editor for [IsRunConfiguration]. Built with plain Swing so no Kotlin UI DSL dependency
 * is required at the current platform level.
 */
class IsRunConfigurationEditor(private val project: Project) : SettingsEditor<IsRunConfiguration>() {

    private val scriptPathLabel = JLabel()

    private val dryRunRadio = JRadioButton(PluginBundle.message("run.config.editor.run_mode.dry"))
    private val realRunRadio = JRadioButton(PluginBundle.message("run.config.editor.run_mode.real"))
    private val runModeGroup = ButtonGroup().also { g -> g.add(dryRunRadio); g.add(realRunRadio) }

    private val installRadio = JRadioButton(PluginBundle.message("run.config.editor.action_type.install"))
    private val uninstallRadio = JRadioButton(PluginBundle.message("run.config.editor.action_type.uninstall"))
    private val actionGroup = ButtonGroup().also { g -> g.add(installRadio); g.add(uninstallRadio) }

    private val uninstallerDirField = JTextField()
    private val uninstallerDirLabel = JLabel(PluginBundle.message("run.config.editor.uninstaller_dir") + ":")
    private val uninstallerBrowse = JButton("…")

    private val languageLabel = JLabel(PluginBundle.message("run.config.editor.language") + ":")
    private val languageCombo = JComboBox<String>()

    private val debugOutputCheck = JCheckBox(PluginBundle.message("run.config.editor.debug_output"))

    private val panel: JPanel = buildPanel()

    init {
        uninstallRadio.addActionListener { updateUninstallerVisibility() }
        installRadio.addActionListener { updateUninstallerVisibility() }
        uninstallerBrowse.addActionListener {
            val chooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                uninstallerDirField.text.takeIf { it.isNotBlank() }?.let { currentDirectory = File(it) }
            }
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                uninstallerDirField.text = chooser.selectedFile.absolutePath
            }
        }
    }

    private fun buildPanel(): JPanel {
        val p = JPanel(GridBagLayout())
        val HORIZONTAL = GridBagConstraints.HORIZONTAL
        val NONE = GridBagConstraints.NONE
        fun gc(row: Int, col: Int, fill: Int = HORIZONTAL, gridwidth: Int = 1, insets: Insets = Insets(2, 4, 2, 4)) =
            GridBagConstraints(col, row, gridwidth, 1, 1.0, 0.0, GridBagConstraints.WEST, fill, insets, 0, 0)

        p.add(JLabel(PluginBundle.message("run.config.editor.script") + ":"), gc(0, 0, NONE))
        p.add(scriptPathLabel, gc(0, 1))

        p.add(JLabel(PluginBundle.message("run.config.editor.run_mode") + ":"), gc(1, 0, NONE))
        val modePanel = JPanel().apply { add(dryRunRadio); add(realRunRadio) }
        p.add(modePanel, gc(1, 1))

        p.add(JLabel(PluginBundle.message("run.config.editor.action_type") + ":"), gc(2, 0, NONE))
        val actionPanel = JPanel().apply { add(installRadio); add(uninstallRadio) }
        p.add(actionPanel, gc(2, 1))

        p.add(uninstallerDirLabel, gc(3, 0, NONE))
        val udirPanel = JPanel(GridBagLayout()).also { up ->
            up.add(uninstallerDirField, gc(0, 0))
            up.add(uninstallerBrowse, gc(0, 1, NONE, 1, Insets(2, 2, 2, 4)))
        }
        p.add(udirPanel, gc(3, 1))

        p.add(languageLabel, gc(4, 0, NONE))
        p.add(languageCombo, gc(4, 1))

        p.add(debugOutputCheck, gc(5, 1))

        return p
    }

    private fun updateUninstallerVisibility() {
        val uninstall = uninstallRadio.isSelected
        uninstallerDirLabel.isVisible = uninstall
        uninstallerDirField.isVisible = uninstall
        uninstallerBrowse.isVisible = uninstall
    }

    private fun loadLanguages(scriptPath: String) {
        languageCombo.removeAllItems()
        languageCombo.addItem(PluginBundle.message("run.config.editor.language.default"))
        if (scriptPath.isBlank()) {
            languageLabel.isVisible = false
            languageCombo.isVisible = false
            return
        }
        val names = ReadAction.compute<List<String>, RuntimeException> {
            val vf = LocalFileSystem.getInstance().findFileByPath(scriptPath.replace('\\', '/')) ?: return@compute emptyList()
            val psi = PsiManager.getInstance(project).findFile(vf) as? IsScriptFile ?: return@compute emptyList()
            psi.findSections("Languages")
                .flatMap { it.nameDeclarations }
                .map { it.valueUnquoted }
                .filter { it.isNotBlank() }
        }
        names.forEach { languageCombo.addItem(it) }
        val show = names.size >= 2
        languageLabel.isVisible = show
        languageCombo.isVisible = show
    }

    override fun resetEditorFrom(config: IsRunConfiguration) {
        scriptPathLabel.text = config.scriptPath
        dryRunRadio.isSelected = config.runMode == IsRunMode.DRY
        realRunRadio.isSelected = config.runMode == IsRunMode.REAL
        installRadio.isSelected = config.actionType == IsRunActionType.INSTALL
        uninstallRadio.isSelected = config.actionType == IsRunActionType.UNINSTALL
        uninstallerDirField.text = config.uninstallerDir
        debugOutputCheck.isSelected = config.debugOutput
        loadLanguages(config.scriptPath)
        val idx = (0 until languageCombo.itemCount).firstOrNull { languageCombo.getItemAt(it) == config.languageOverride } ?: 0
        languageCombo.selectedIndex = idx
        updateUninstallerVisibility()
    }

    override fun applyEditorTo(config: IsRunConfiguration) {
        config.runMode = if (dryRunRadio.isSelected) IsRunMode.DRY else IsRunMode.REAL
        config.actionType = if (installRadio.isSelected) IsRunActionType.INSTALL else IsRunActionType.UNINSTALL
        config.uninstallerDir = uninstallerDirField.text.trim()
        config.debugOutput = debugOutputCheck.isSelected
        val selLang = languageCombo.selectedItem as? String ?: ""
        config.languageOverride = if (selLang == PluginBundle.message("run.config.editor.language.default")) "" else selLang
    }

    override fun createEditor(): JComponent = panel
}
