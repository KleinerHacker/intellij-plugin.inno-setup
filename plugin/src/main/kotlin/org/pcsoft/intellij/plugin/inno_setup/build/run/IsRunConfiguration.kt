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
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
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

    /**
     * Name of the [IsBuildConfiguration] this run compiles with — the compile options themselves
     * (preprocessor symbols, output override, extra ISCC options) live there and are deliberately *not*
     * copied here, so editing a build configuration takes effect in every run that references it.
     *
     * Consequently this name is the only build-related part of the run configuration's identity.
     */
    var buildConfigurationName: String = ""

    /** Environment variables passed to the launched installer (setup.exe). */
    var envData: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT

    /**
     * Hidden (not shown in the editor) — persistent temporary output directory, used only when the
     * project is configured for [org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode.DRY]
     * (which would otherwise produce no real `setup.exe`). Stays attached to the run configuration so
     * the generated installer can be reused across runs.
     */
    var persistentTempOutputDir: String = ""

    /**
     * The referenced build configuration, or `null` when none is selected or the referenced one has been
     * deleted. Callers that need a usable one should fall back to
     * [IsBuildConfigurationService.defaultConfiguration].
     */
    fun buildConfiguration(): IsBuildConfiguration? =
        IsBuildConfigurationService.getInstance(project).byName(buildConfigurationName)

    override fun getConfigurationEditor(): SettingsEditor<out IsRunConfiguration> =
        IsRunConfigurationEditor(project)

    override fun checkConfiguration() {
        if (scriptPath.isBlank())
            throw RuntimeConfigurationException(PluginBundle.message("run.config.error.no_script"))
        if (!File(scriptPath).isFile)
            throw RuntimeConfigurationException(PluginBundle.message("run.config.error.script_missing", scriptPath))
        if (buildConfigurationName.isBlank())
            throw RuntimeConfigurationException(PluginBundle.message("run.config.error.no_build_config"))
        if (buildConfiguration() == null)
            throw RuntimeConfigurationException(
                PluginBundle.message("run.config.error.build_config_missing", buildConfigurationName)
            )
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): IsRunProfileState =
        IsRunProfileState(this, environment)

    override fun readExternal(element: Element) {
        super.readExternal(element)
        scriptPath = element.getAttributeValue("scriptPath") ?: ""
        languageOverride = element.getAttributeValue("languageOverride") ?: ""
        debugOutput = element.getAttributeValue("debugOutput")?.toBoolean() ?: true
        buildConfigurationName = element.getAttributeValue("buildConfigurationName") ?: ""
        persistentTempOutputDir = element.getAttributeValue("persistentTempOutputDir") ?: ""
        envData = EnvironmentVariablesData.readExternal(element)
        migrateLegacyDefines(element.getAttributeValue(LEGACY_DEFINES_ATTRIBUTE))
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.setAttribute("scriptPath", scriptPath)
        element.setAttribute("languageOverride", languageOverride)
        element.setAttribute("debugOutput", debugOutput.toString())
        element.setAttribute("buildConfigurationName", buildConfigurationName)
        element.setAttribute("persistentTempOutputDir", persistentTempOutputDir)
        envData.writeExternal(element)
    }

    /**
     * Moves the symbols of a run configuration written before build configurations existed into one, so an
     * upgraded project keeps compiling with the symbols it used to.
     *
     * An existing configuration defining exactly the same symbols is reused rather than duplicated; only
     * when none matches is a new one created, named after this run configuration.
     */
    private fun migrateLegacyDefines(legacyDefines: String?) {
        if (buildConfigurationName.isNotBlank()) return
        val defines = legacyDefines?.trim().orEmpty()
        val service = IsBuildConfigurationService.getInstance(project)
        if (defines.isEmpty()) {
            buildConfigurationName = service.defaultConfiguration()?.name ?: ""
            return
        }

        val wanted = IsBuildConfiguration("", compilerSymbols = defines).contentHash()
        val existing = service.all().firstOrNull { it.contentHash() == wanted }
        if (existing != null) {
            buildConfigurationName = existing.name
            return
        }

        val migrated = IsBuildConfiguration(uniqueMigratedName(service), compilerSymbols = defines)
        service.save(migrated)
        buildConfigurationName = migrated.name
    }

    private fun uniqueMigratedName(service: IsBuildConfigurationService): String {
        val taken = service.all().map { it.name }.toSet()
        val base = PluginBundle.message("build.config.migrated_name", name)
        if (base !in taken) return base
        return generateSequence(2) { it + 1 }.map { "$base ($it)" }.first { it !in taken }
    }

    private companion object {
        /** Attribute that held the symbols before they moved into a build configuration. */
        const val LEGACY_DEFINES_ATTRIBUTE = "compilerDefines"
    }
}
