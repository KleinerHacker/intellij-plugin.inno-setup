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

package org.pcsoft.intellij.plugin.inno_setup.build.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.build.IsCompilerService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector

/**
 * Context-menu action that compiles a single `.iss` script with ISCC. It is shown only on a single
 * `.iss` file and is greyed out — with the reason as its hover description — for scripts that are
 * included by another script and therefore must not be built standalone.
 */
class IsScriptBuildAction : DumbAwareAction({ PluginBundle.message("action.iss.build_script.text") }) {

    /**
     * Runs on a background thread because the enabled-state check inspects the project's `#include`
     * graph via the index.
     */
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    /**
     * Shows the action only for a single `.iss` file; disables it (with a reason) for included scripts.
     */
    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val project = e.project
        val file = singleScript(e)
        if (project == null || file == null) {
            presentation.isVisible = false
            return
        }
        presentation.isVisible = true
        if (IsScriptCollector(project).isIncludedByOther(file)) {
            presentation.isEnabled = false
            presentation.description = PluginBundle.message("action.iss.build_script.included_reason")
        } else {
            presentation.isEnabled = true
            presentation.description = PluginBundle.message("action.iss.build_script.description")
        }
    }

    /**
     * Compiles the selected script.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = singleScript(e) ?: return
        project.service<IsCompilerService>().compileScript(file)
    }

    /** The single selected `.iss` file, or `null` for any other selection. */
    private fun singleScript(e: AnActionEvent): VirtualFile? {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (files != null && files.size > 1) return null
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        if (file.isDirectory || !file.extension.equals("iss", ignoreCase = true)) return null
        return file
    }
}
