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

package org.pcsoft.intellij.plugin.inno_setup.build.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint
import org.pcsoft.intellij.plugin.inno_setup.build.action.IsBuildConfigurationActions
import org.pcsoft.intellij.plugin.inno_setup.build.action.IsScriptRunAction
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTitle
import java.awt.event.MouseEvent
import javax.swing.Icon

/**
 * Renders a play icon in the gutter next to the `\[Setup]` section header. Clicking it offers the project's
 * build configurations and runs the current script with the chosen one.
 */
class IsSetupSectionGutterIconProvider : LineMarkerProvider {

    // getLineMarkerInfo is the legacy single-element entry point; collectSlowLineMarkers is preferred.
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        for (element in elements) {
            val title = element as? IsSectionTitle ?: continue
            if (!title.text.equals("Setup", ignoreCase = true)) continue
            val scriptFile = title.containingFile?.virtualFile ?: continue
            // Line markers must be anchored on a leaf element (the IDENTIFIER), not the composite title.
            val anchor = title.firstChild ?: continue

            result += LineMarkerInfo(
                anchor,
                anchor.textRange,
                AllIcons.RunConfigurations.TestState.Run,
                { PluginBundle.message("gutter.run_setup.tooltip") },
                { mouseEvent, elt -> showBuildConfigurationPopup(elt.project, scriptFile, mouseEvent) },
                GutterIconRenderer.Alignment.RIGHT,
                // Accessible name for screen readers; the tooltip already states what the icon does.
                { PluginBundle.message("gutter.run_setup.tooltip") }
            )
        }
    }

    /**
     * Opens the list of build configurations at the click position and runs [scriptFile] with the one
     * picked. Always a list, even for a single configuration, so the gutter never silently compiles with
     * options the user did not see.
     */
    private fun showBuildConfigurationPopup(project: Project, scriptFile: VirtualFile, mouseEvent: MouseEvent) {
        val configs = IsBuildConfigurationActions.availableConfigurations(project)
        if (configs.isEmpty()) {
            IsScriptRunAction.runScript(project, scriptFile.path, scriptFile.nameWithoutExtension)
            return
        }

        val step = object : BaseListPopupStep<IsBuildConfiguration>(
            PluginBundle.message("gutter.run_setup.popup_title"), configs
        ) {
            override fun getTextFor(value: IsBuildConfiguration): String = value.name
            override fun getIconFor(value: IsBuildConfiguration): Icon = IsBuildConfigurationActions.ICON

            override fun onChosen(selectedValue: IsBuildConfiguration, finalChoice: Boolean): PopupStep<*>? =
                doFinalStep {
                    IsScriptRunAction.runScript(
                        project, scriptFile.path, scriptFile.nameWithoutExtension, selectedValue.name
                    )
                }
        }
        JBPopupFactory.getInstance().createListPopup(step).show(RelativePoint(mouseEvent))
    }
}
