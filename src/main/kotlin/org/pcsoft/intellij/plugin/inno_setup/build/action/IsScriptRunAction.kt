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

import com.intellij.execution.ExecutionManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunActionType
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationType
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunMode

/**
 * Context-menu action that runs a single `.iss` script as a dry-run installation. It creates
 * a ghost run configuration and immediately executes it. Greyed out for included scripts.
 */
class IsScriptRunAction : DumbAwareAction({ PluginBundle.message("action.iss.run_script.text") }) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

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
            presentation.description = PluginBundle.message("action.iss.run_script.included_reason")
        } else {
            presentation.isEnabled = true
            presentation.description = PluginBundle.message("action.iss.run_script.description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project ?: return
        val file = singleScript(e) ?: return
        runScript(e, file.path, file.nameWithoutExtension)
    }

    private fun singleScript(e: AnActionEvent): VirtualFile? {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (files != null && files.size > 1) return null
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        if (file.isDirectory || !file.extension.equals("iss", ignoreCase = true)) return null
        return file
    }

    companion object {
        fun runScript(e: AnActionEvent, scriptPath: String, name: String) {
            val project = e.project ?: return
            val type = IsRunConfigurationType()
            val factory = type.configurationFactories.first()
            val config = factory.createTemplateConfiguration(project) as IsRunConfiguration
            config.name = name
            config.scriptPath = scriptPath
            config.runMode = IsRunMode.DRY
            config.actionType = IsRunActionType.INSTALL
            config.debugOutput = true

            val executor = DefaultRunExecutor.getRunExecutorInstance()
            val env = ExecutionEnvironmentBuilder.create(project, executor, config).build()
            ExecutionManager.getInstance(project).restartRunProfile(env)
        }
    }
}
