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

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.ui.components.ActionLink
import com.intellij.ui.dsl.listCellRenderer.listCellRenderer
import com.intellij.util.ui.JBUI
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.valueUnquoted
import org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsConfigurable
import java.awt.*
import javax.swing.*

/**
 * Settings editor for [IsRunConfiguration]. Built with plain Swing so no Kotlin UI DSL dependency
 * is required at the current platform level.
 */
class IsRunConfigurationEditor(private val project: Project) : SettingsEditor<IsRunConfiguration>() {

    // Editable combo: lets the user pick any project `.iss` (including included ones) or type a path,
    // which is required to create a run configuration from scratch.
    private val scriptCombo = ComboBox<String>().apply { isEditable = true }

    private val languageLabel = JLabel(PluginBundle.message("run.config.editor.language") + ":")
    private val languageCombo = ComboBox<String>()
    private val languageRow = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
        alignmentX = Component.LEFT_ALIGNMENT
        add(languageLabel)
        add(Box.createHorizontalStrut(4))
        add(languageCombo)
    }

    private val debugOutputCheck = JCheckBox(PluginBundle.message("run.config.editor.debug_output"))
        .apply { alignmentX = Component.LEFT_ALIGNMENT }

    private val envVarsComponent = EnvironmentVariablesComponent(project)
        .apply { alignmentX = Component.LEFT_ALIGNMENT }

    // Which build configuration the run compiles with. Only the *name* is part of the run configuration;
    // the symbols and options behind it are edited on the project's build settings page — hence the link.
    private val buildConfigCombo = ComboBox<String>().apply {
        toolTipText = PluginBundle.message("run.config.editor.build_config.tooltip")
        @Suppress("UNCHECKED_CAST")
        renderer = listCellRenderer<String>("") {
            icon(AllIcons.General.GearPlain)
            text(value)
        } as ListCellRenderer<String>
    }
    private val configureLink = ActionLink(PluginBundle.message("run.config.editor.build_config.configure")) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, IsBuildSettingsConfigurable::class.java)
        loadBuildConfigurations(selectedBuildConfiguration())
    }
    private val buildConfigRow = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
        alignmentX = Component.LEFT_ALIGNMENT
        add(JLabel(PluginBundle.message("run.config.editor.build_config") + ":"))
        add(Box.createHorizontalStrut(4))
        add(buildConfigCombo.apply { preferredSize = Dimension(280, preferredSize.height) })
        add(Box.createHorizontalStrut(8))
        add(configureLink)
    }

    private val panel: JPanel = buildPanel()

    init {
        scriptCombo.addActionListener { loadLanguages(selectedScriptPath()) }
    }

    private fun buildPanel(): JPanel {
        val p = JPanel(GridBagLayout())
        val HORIZONTAL = GridBagConstraints.HORIZONTAL
        val NONE = GridBagConstraints.NONE
        fun gc(
            row: Int,
            col: Int,
            fill: Int = HORIZONTAL,
            gridwidth: Int = 1,
            weightx: Double = 1.0,
            insets: Insets = JBUI.insets(
                2,
                4
            )
        ) =
            GridBagConstraints(col, row, gridwidth, 1, weightx, 0.0, GridBagConstraints.WEST, fill, insets, 0, 0)

        // Script row.
        p.add(JLabel(PluginBundle.message("run.config.editor.script") + ":"), gc(0, 0, NONE, weightx = 0.0))
        p.add(scriptCombo, gc(0, 1))

        // Options heading.
        val heading = JLabel(PluginBundle.message("run.config.editor.options.title"))
            .apply { font = font.deriveFont(Font.BOLD) }
        p.add(heading, gc(1, 0, NONE, gridwidth = 2, weightx = 0.0, insets = JBUI.insets(12, 4, 4, 4)))

        // Vertical, left-aligned list of run options under the heading.
        val options = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
            border = BorderFactory.createEmptyBorder(0, 12, 0, 0)
            add(buildConfigRow)
            add(Box.createVerticalStrut(4))
            add(languageRow)
            add(Box.createVerticalStrut(4))
            add(debugOutputCheck)
            add(Box.createVerticalStrut(4))
            add(envVarsComponent)
        }
        p.add(options, gc(2, 0, HORIZONTAL, gridwidth = 2, insets = JBUI.insets(0, 4, 2, 4)))

        // Push everything to the top.
        p.add(
            JPanel().apply { isOpaque = false }, GridBagConstraints(
                0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        )

        return p
    }

    private fun selectedScriptPath(): String =
        (scriptCombo.editor.item as? String ?: scriptCombo.selectedItem as? String ?: "").trim()

    private fun selectedBuildConfiguration(): String = buildConfigCombo.selectedItem as? String ?: ""

    private fun loadScripts(current: String) {
        scriptCombo.removeAllItems()
        val paths = ApplicationManager.getApplication().runReadAction(Computable {
            IsScriptCollector(project).allScripts().map { it.path }.sorted()
        })
        paths.forEach { scriptCombo.addItem(it) }
        // For an editable combo this also sets the editor text even if the path is not in the list.
        scriptCombo.selectedItem = current
        // Keep the combo from stretching too wide in the dialog.
        scriptCombo.preferredSize = Dimension(360, scriptCombo.preferredSize.height)
    }

    /**
     * Fills the combo with the project's build configurations. A [current] name that no longer exists is
     * still offered, so a stale reference stays visible (and reported by `checkConfiguration`) instead of
     * silently turning into a different configuration.
     */
    private fun loadBuildConfigurations(current: String) {
        buildConfigCombo.removeAllItems()
        val names = IsBuildConfigurationService.getInstance(project).all().map { it.name }
        names.forEach { buildConfigCombo.addItem(it) }
        if (current.isNotBlank() && current !in names) buildConfigCombo.addItem(current)
        buildConfigCombo.selectedItem = current.ifBlank { names.firstOrNull() ?: "" }
    }

    private fun loadLanguages(scriptPath: String) {
        languageCombo.removeAllItems()
        languageCombo.addItem(PluginBundle.message("run.config.editor.language.default"))
        if (scriptPath.isBlank()) {
            languageRow.isVisible = false
            return
        }
        val names = ApplicationManager.getApplication().runReadAction(Computable {
            val vf = LocalFileSystem.getInstance().findFileByPath(scriptPath.replace('\\', '/'))
                ?: return@Computable emptyList()
            val psi = PsiManager.getInstance(project).findFile(vf) as? IsScriptFile ?: return@Computable emptyList()
            psi.findSections("Languages")
                .flatMap { it.nameDeclarations }
                .map { it.valueUnquoted }
                .filter { it.isNotBlank() }
        })
        names.forEach { languageCombo.addItem(it) }
        languageRow.isVisible = names.size >= 2
    }

    override fun resetEditorFrom(config: IsRunConfiguration) {
        loadScripts(config.scriptPath)
        loadBuildConfigurations(config.buildConfigurationName)
        debugOutputCheck.isSelected = config.debugOutput
        envVarsComponent.envData = config.envData
        loadLanguages(config.scriptPath)
        val idx =
            (0 until languageCombo.itemCount).firstOrNull { languageCombo.getItemAt(it) == config.languageOverride }
                ?: 0
        languageCombo.selectedIndex = idx
    }

    override fun applyEditorTo(config: IsRunConfiguration) {
        config.scriptPath = selectedScriptPath()
        config.buildConfigurationName = selectedBuildConfiguration()
        config.debugOutput = debugOutputCheck.isSelected
        config.envData = envVarsComponent.envData
        val selLang = languageCombo.selectedItem as? String ?: ""
        config.languageOverride =
            if (selLang == PluginBundle.message("run.config.editor.language.default")) "" else selLang
    }

    override fun createEditor(): JComponent = panel
}
