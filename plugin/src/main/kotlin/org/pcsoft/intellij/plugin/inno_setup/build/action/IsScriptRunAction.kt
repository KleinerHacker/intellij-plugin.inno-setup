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
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle

/**
 * Runs a single `.iss` script with one named build configuration. It creates a ghost run configuration and
 * immediately executes it; it is not registered in `plugin.xml` but instantiated per build configuration by
 * [IsScriptRunGroup]. Greyed out for included scripts.
 */
class IsScriptRunAction(private val buildConfigName: String) :
    DumbAwareAction({ buildConfigName }, { PluginBundle.message("action.iss.run_script.description") }, IsBuildConfigurationActions.ICON) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        IsScriptActionSupport.updatePresentation(
            e,
            e.presentation,
            PluginBundle.message("action.iss.run_script.included_reason"),
            PluginBundle.message("action.iss.run_script.description")
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = IsScriptActionSupport.singleScript(e) ?: return
        runScript(project, file.path, file.nameWithoutExtension, buildConfigName)
    }

    companion object {

        /**
         * Compiles and runs the script at [scriptPath] with the build configuration called
         * [buildConfigName], under a ghost run configuration named [name].
         *
         * The build configuration is referenced by name only — the same identity rule the persisted run
         * configurations follow — so the run always picks up the configuration's current content.
         */
        fun runScript(project: Project, scriptPath: String, name: String, buildConfigName: String) {
            val type = IsRunConfigurationType()
            val factory = type.configurationFactories.first()
            val config = factory.createTemplateConfiguration(project) as IsRunConfiguration
            config.name = name
            config.scriptPath = scriptPath
            config.debugOutput = true
            config.buildConfigurationName = buildConfigName

            val executor = DefaultRunExecutor.getRunExecutorInstance()
            val env = ExecutionEnvironmentBuilder.create(project, executor, config).build()
            ExecutionManager.getInstance(project).restartRunProfile(env)
        }

        /**
         * Runs [scriptPath] with the project's default build configuration — the fallback for callers that
         * do not offer a choice.
         */
        fun runScript(project: Project, scriptPath: String, name: String) {
            val fallback = IsBuildConfigurationService.getInstance(project)
                .also { it.ensureDefaults() }
                .defaultConfiguration()
                ?: IsBuildConfiguration(IsBuildConfiguration.DEFAULT_DEBUG_NAME, compilerSymbols = "DEBUG")
            runScript(project, scriptPath, name, fallback.name)
        }
    }
}
