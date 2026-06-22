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
import com.intellij.openapi.actionSystem.ActionUiKind
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.build.action.IsScriptRunAction
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTitle

/**
 * Renders a play icon in the gutter next to the `[Setup]` section header. Clicking it triggers a
 * dry-run installation of the current script.
 */
class IsSetupSectionGutterIconProvider : LineMarkerProvider {

    // getLineMarkerInfo is the legacy single-element entry point; collectSlowLineMarkers is preferred.
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    @Suppress("DEPRECATION")
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
                { _, elt ->
                    val dataContext = DataContext { dataId ->
                        when (dataId) {
                            CommonDataKeys.PROJECT.name -> elt.project
                            CommonDataKeys.VIRTUAL_FILE.name -> scriptFile
                            CommonDataKeys.VIRTUAL_FILE_ARRAY.name -> arrayOf(scriptFile)
                            else -> null
                        }
                    }
                    val event = AnActionEvent.createEvent(
                        dataContext, null, "gutter", ActionUiKind.NONE, null
                    )
                    IsScriptRunAction.runScript(event, scriptFile.path, scriptFile.nameWithoutExtension)
                },
                GutterIconRenderer.Alignment.RIGHT
            )
        }
    }
}
