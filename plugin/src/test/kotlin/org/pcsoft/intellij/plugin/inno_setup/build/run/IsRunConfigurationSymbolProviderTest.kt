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

import com.intellij.execution.RunManager
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Tests that the editor's `#ifdef` analysis is seeded from the *selected* run configuration's build
 * configuration — the same options the next run will compile with.
 */
class IsRunConfigurationSymbolProviderTest : IsTimedBasePlatformTestCase() {

    private val provider = IsRunConfigurationSymbolProvider()

    private lateinit var configDirectory: File
    private lateinit var script: VirtualFile

    override fun setUp() {
        super.setUp()
        configDirectory = File.createTempFile("inno-symbols", "").let { probe ->
            probe.delete()
            File(probe.parentFile, probe.name + "-" + IsBuildConfigurationService.DIRECTORY_NAME)
        }
        IsBuildConfigurationService.getInstance(project).directoryOverride = configDirectory
        script = myFixture.addFileToProject("setup.iss", "[Setup]\nAppName=x\n").virtualFile
    }

    override fun tearDown() {
        try {
            IsBuildConfigurationService.getInstance(project).directoryOverride = null
            configDirectory.listFiles()?.forEach { it.delete() }
            configDirectory.delete()
        } finally {
            super.tearDown()
        }
    }

    /** Registers a run configuration referencing [buildConfigName] and makes it the selected one. */
    private fun select(name: String, buildConfigName: String) {
        val runManager = RunManager.getInstance(project)
        val factory = IsRunConfigurationType().configurationFactories.first()
        val settings = runManager.createConfiguration(name, factory)
        (settings.configuration as IsRunConfiguration).apply {
            scriptPath = script.path
            buildConfigurationName = buildConfigName
        }
        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings
    }

    /** Without a selected Inno Setup configuration nothing is contributed, keeping conditions undecided. */
    fun testNoSelectionContributesNothing() {
        assertTrue(provider.symbols(project, script).isEmpty())
    }

    /** The selected configuration's build configuration supplies the symbols. */
    fun testSelectedConfigurationContributesItsSymbols() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG,VERSION=2"))
        select("run debug", "Debug")

        assertEquals(mapOf("DEBUG" to null, "VERSION" to "2"), provider.symbols(project, script))
    }

    /** Switching the selected configuration switches the symbols — Debug and Release must not blend. */
    fun testSwitchingSelectionSwitchesSymbols() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.save(IsBuildConfiguration("Debug", "DEBUG"))
        service.save(IsBuildConfiguration("Release", "RELEASE"))
        select("run debug", "Debug")
        assertEquals(mapOf("DEBUG" to null), provider.symbols(project, script))

        select("run release", "Release")

        assertEquals(mapOf("RELEASE" to null), provider.symbols(project, script))
    }

    /** Editing the referenced build configuration takes effect without touching the run configuration. */
    fun testEditedBuildConfigurationIsPickedUp() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.save(IsBuildConfiguration("Debug", "DEBUG"))
        select("run debug", "Debug")

        service.save(IsBuildConfiguration("Debug", "DEBUG,TRACE"))

        assertEquals(mapOf("DEBUG" to null, "TRACE" to null), provider.symbols(project, script))
    }

    /** A reference to a deleted build configuration contributes nothing rather than stale symbols. */
    fun testMissingBuildConfigurationContributesNothing() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("run debug", "Debug")
        IsBuildConfigurationService.getInstance(project).delete("Debug")

        assertTrue(provider.symbols(project, script).isEmpty())
    }

    /** The symbols apply to every script, since the selected configuration is a project-wide choice. */
    fun testSymbolsApplyToOtherScriptsToo() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("run debug", "Debug")
        val other = myFixture.addFileToProject("other.iss", "[Setup]\n").virtualFile

        assertEquals(mapOf("DEBUG" to null), provider.symbols(project, other))
    }
}
