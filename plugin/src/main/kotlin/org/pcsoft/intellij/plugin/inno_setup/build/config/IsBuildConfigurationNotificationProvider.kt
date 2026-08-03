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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import com.intellij.ui.dsl.listCellRenderer.listCellRenderer
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfiguration
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import java.util.function.Function
import javax.swing.JComponent
import javax.swing.ListCellRenderer

/**
 * Warns above an `.iss` editor that no Inno Setup run configuration is selected, and therefore no build
 * configuration supplies the `/D…` symbols the conditional-branch analysis needs.
 *
 * Without this the greyed-out `#if` branches would silently be the *undecided* ones rather than the ones the
 * next build actually skips — a difference the user has no other way of noticing. The banner embeds the list
 * of Inno Setup run configurations so the situation can be fixed where it is reported.
 */
class IsBuildConfigurationNotificationProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?>? {
        if (file.fileType !is IsScriptFileType) return null

        val runManager = RunManager.getInstance(project)
        val selected = runManager.selectedConfiguration?.configuration
        if (selected is IsRunConfiguration && selected.buildConfiguration() != null) return null

        val candidates = runManager.allSettings.filter { it.configuration is IsRunConfiguration }
        val message = when {
            selected is IsRunConfiguration -> PluginBundle.message(
                "editor.notification.build_config_missing", selected.buildConfigurationName
            )

            else -> PluginBundle.message("editor.notification.no_run_config")
        }

        return Function { _ -> createPanel(project, message, candidates) }
    }

    private fun createPanel(
        project: Project,
        message: String,
        candidates: List<RunnerAndConfigurationSettings>
    ): JComponent {
        // Subclassed so the combo can go into the panel's own links area on the right; adding it to the
        // panel directly would land in BorderLayout.CENTER and replace the message label.
        val panel = object : EditorNotificationPanel(Status.Warning) {
            fun addChooser(component: JComponent) = myLinksPanel.add(component)
        }
        panel.icon(AllIcons.General.GearPlain)
        panel.text = message

        if (candidates.isNotEmpty()) {
            val combo = ComboBox<RunnerAndConfigurationSettings>().apply {
                @Suppress("UNCHECKED_CAST")
                renderer = listCellRenderer<RunnerAndConfigurationSettings>("") {
                    icon(AllIcons.General.GearPlain)
                    text(value.name)
                } as ListCellRenderer<RunnerAndConfigurationSettings>
                candidates.forEach { addItem(it) }
                selectedItem = null
                addActionListener {
                    val chosen = selectedItem as? RunnerAndConfigurationSettings ?: return@addActionListener
                    select(project, chosen)
                }
            }
            panel.addChooser(combo)
        }

        return panel
    }

    /**
     * Makes [settings] the selected run configuration and re-runs the analysis, so the symbols it brings
     * take effect in the open editors immediately instead of on the next typing.
     */
    private fun select(project: Project, settings: RunnerAndConfigurationSettings) {
        RunManager.getInstance(project).selectedConfiguration = settings
        DaemonCodeAnalyzer.getInstance(project).restart(RESTART_REASON)
        EditorNotifications.getInstance(project).updateAllNotifications()
    }

    private companion object {
        /** Diagnostic reason shown in the daemon's restart log. */
        const val RESTART_REASON = "Inno Setup run configuration selected from the editor banner"
    }
}
