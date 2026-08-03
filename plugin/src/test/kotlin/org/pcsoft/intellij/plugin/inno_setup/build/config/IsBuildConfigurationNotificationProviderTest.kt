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

import com.intellij.execution.RunManager
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Tests when the editor warns that no build configuration supplies the preprocessor symbols.
 */
class IsBuildConfigurationNotificationProviderTest : IsTimedBasePlatformTestCase() {

    private val provider = IsBuildConfigurationNotificationProvider()

    private lateinit var configDirectory: File
    private lateinit var script: VirtualFile

    override fun setUp() {
        super.setUp()
        configDirectory = File.createTempFile("inno-notification", "").let { probe ->
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

    private fun select(buildConfigName: String) {
        val runManager = RunManager.getInstance(project)
        val settings = runManager.createConfiguration(
            "run", IsRunConfigurationType().configurationFactories.first()
        )
        (settings.configuration as IsRunConfiguration).apply {
            scriptPath = script.path
            buildConfigurationName = buildConfigName
        }
        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings
    }

    /** Without a selected Inno Setup run configuration the script is flagged. */
    fun testWarnsWithoutSelectedRunConfiguration() {
        assertNotNull(provider.collectNotificationData(project, script))
    }

    /** With a selected configuration pointing at an existing build configuration there is nothing to warn about. */
    fun testSilentForResolvableSelection() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("Debug")

        assertNull(provider.collectNotificationData(project, script))
    }

    /** A selection referring to a deleted build configuration is flagged, not silently ignored. */
    fun testWarnsForMissingBuildConfiguration() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("Debug")
        IsBuildConfigurationService.getInstance(project).delete("Debug")

        assertNotNull(provider.collectNotificationData(project, script))
    }

    /** The warning belongs to `.iss` scripts only; other files are none of its business. */
    fun testIgnoresNonScriptFiles() {
        val text = myFixture.addFileToProject("readme.txt", "hi").virtualFile
        assertNull(provider.collectNotificationData(project, text))
    }
}
