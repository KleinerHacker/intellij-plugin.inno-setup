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

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import java.io.File

/**
 * Run configuration that compiles an ISS script and launches the generated installer.
 */
class IsRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<Element>(project, factory, name) {

    var scriptPath: String = ""
    var languageOverride: String = ""
    var debugOutput: Boolean = true

    /** Environment variables passed to the launched installer (setup.exe). */
    var envData: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT

    /**
     * Hidden (not shown in the editor) — persistent temporary output directory, used only when the
     * project is configured for [org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode.DRY]
     * (which would otherwise produce no real `setup.exe`). Stays attached to the run configuration so
     * the generated installer can be reused across runs.
     */
    var persistentTempOutputDir: String = ""

    override fun getConfigurationEditor(): SettingsEditor<out IsRunConfiguration> =
        IsRunConfigurationEditor(project)

    override fun checkConfiguration() {
        if (scriptPath.isBlank())
            throw RuntimeConfigurationException(PluginBundle.message("run.config.error.no_script"))
        if (!File(scriptPath).isFile)
            throw RuntimeConfigurationException(PluginBundle.message("run.config.error.script_missing", scriptPath))
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): IsRunProfileState =
        IsRunProfileState(this, environment)

    override fun readExternal(element: Element) {
        super.readExternal(element)
        scriptPath = element.getAttributeValue("scriptPath") ?: ""
        languageOverride = element.getAttributeValue("languageOverride") ?: ""
        debugOutput = element.getAttributeValue("debugOutput")?.toBoolean() ?: true
        persistentTempOutputDir = element.getAttributeValue("persistentTempOutputDir") ?: ""
        envData = EnvironmentVariablesData.readExternal(element)
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.setAttribute("scriptPath", scriptPath)
        element.setAttribute("languageOverride", languageOverride)
        element.setAttribute("debugOutput", debugOutput.toString())
        element.setAttribute("persistentTempOutputDir", persistentTempOutputDir)
        envData.writeExternal(element)
    }
}
