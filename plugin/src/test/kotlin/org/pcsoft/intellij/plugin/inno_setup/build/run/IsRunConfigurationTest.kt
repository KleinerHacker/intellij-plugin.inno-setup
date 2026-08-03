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

import com.intellij.execution.configurations.RuntimeConfigurationException
import org.jdom.Element
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File
import java.io.File.createTempFile

class IsRunConfigurationTest : IsTimedBasePlatformTestCase() {

    private lateinit var configDirectory: File

    override fun setUp() {
        super.setUp()
        configDirectory = createTempFile("inno-run-config", "").let { probe ->
            probe.delete()
            File(probe.parentFile, probe.name + "-" + IsBuildConfigurationService.DIRECTORY_NAME)
        }
        IsBuildConfigurationService.getInstance(project).directoryOverride = configDirectory
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

    private fun config(): IsRunConfiguration {
        val type = IsRunConfigurationType()
        return IsRunConfiguration(project, type.configurationFactories.first(), "test")
    }

    private fun existingScript(): File = createTempFile("setup", ".iss").apply { deleteOnExit() }

    // ── validation ───────────────────────────────────────────────────────────────

    fun testValidationFailsForBlankScriptPath() {
        val cfg = config()
        cfg.scriptPath = ""
        assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
    }

    fun testValidationFailsForMissingFile() {
        val cfg = config()
        cfg.scriptPath = "/nonexistent/path/setup.iss"
        assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
    }

    fun testValidationPassesForValidConfig() {
        val cfg = config()
        // Use a temp file on the real FS so File.isFile() returns true.
        val tmpFile = existingScript()
        try {
            IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
            cfg.scriptPath = tmpFile.absolutePath
            cfg.buildConfigurationName = "Debug"
            cfg.checkConfiguration() // must not throw
        } finally {
            tmpFile.delete()
        }
    }

    /** A run without a build configuration has no compile options and must be rejected. */
    fun testValidationFailsWithoutBuildConfiguration() {
        val cfg = config()
        val tmpFile = existingScript()
        try {
            cfg.scriptPath = tmpFile.absolutePath
            cfg.buildConfigurationName = ""
            assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
        } finally {
            tmpFile.delete()
        }
    }

    /** A reference to a deleted build configuration is an error, not a silent fallback to another one. */
    fun testValidationFailsForDeletedBuildConfiguration() {
        val cfg = config()
        val tmpFile = existingScript()
        try {
            val service = IsBuildConfigurationService.getInstance(project)
            service.save(IsBuildConfiguration("Gone"))
            cfg.scriptPath = tmpFile.absolutePath
            cfg.buildConfigurationName = "Gone"
            cfg.checkConfiguration() // still present

            service.delete("Gone")
            assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
        } finally {
            tmpFile.delete()
        }
    }

    // ── persistence ──────────────────────────────────────────────────────────────

    /** Only the *name* of the build configuration is persisted — never its content. */
    fun testBuildConfigurationIsPersistedByNameOnly() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG", "dist", "/Q"))
        val cfg = config()
        cfg.scriptPath = "C:\\proj\\setup.iss"
        cfg.buildConfigurationName = "Debug"

        val element = Element("configuration")
        cfg.writeExternal(element)

        assertEquals("Debug", element.getAttributeValue("buildConfigurationName"))
        assertNull(element.getAttributeValue("compilerDefines"))
        assertFalse(element.attributes.any { it.value.contains("DEBUG") && it.name != "buildConfigurationName" })
    }

    /** A written configuration reads back with the same build configuration reference. */
    fun testWriteReadRoundTrip() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Release"))
        val written = config()
        written.scriptPath = "C:\\proj\\setup.iss"
        written.buildConfigurationName = "Release"
        val element = Element("configuration")
        written.writeExternal(element)

        val read = config()
        read.readExternal(element)

        assertEquals("Release", read.buildConfigurationName)
        assertEquals("C:\\proj\\setup.iss", read.scriptPath)
    }

    /** The resolved configuration is the current content of the referenced one, not a stored copy. */
    fun testBuildConfigurationResolvesCurrentContent() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.save(IsBuildConfiguration("Debug", "DEBUG"))
        val cfg = config()
        cfg.buildConfigurationName = "Debug"
        assertEquals("DEBUG", cfg.buildConfiguration()?.compilerSymbols)

        service.save(IsBuildConfiguration("Debug", "DEBUG,TRACE"))

        assertEquals("DEBUG,TRACE", cfg.buildConfiguration()?.compilerSymbols)
    }

    // ── migration of pre-build-configuration run configurations ──────────────────

    /** Legacy symbols move into a new build configuration so an upgraded project keeps its symbols. */
    fun testLegacyDefinesAreMigrated() {
        val element = Element("configuration").apply {
            setAttribute("scriptPath", "C:\\proj\\setup.iss")
            setAttribute("compilerDefines", "DEBUG,VERSION=2")
        }

        val cfg = config()
        cfg.readExternal(element)

        assertTrue(cfg.buildConfigurationName.isNotBlank())
        assertEquals(
            mapOf("DEBUG" to null, "VERSION" to "2"),
            cfg.buildConfiguration()?.symbols
        )
    }

    /** Migration reuses an existing configuration with the same symbols instead of duplicating it. */
    fun testLegacyDefinesReuseMatchingConfiguration() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.save(IsBuildConfiguration("Debug", "DEBUG"))
        val before = service.all().size

        val element = Element("configuration").apply { setAttribute("compilerDefines", "DEBUG") }
        val cfg = config()
        cfg.readExternal(element)

        assertEquals("Debug", cfg.buildConfigurationName)
        assertEquals(before, service.all().size)
    }

    /** A legacy configuration without symbols simply adopts the project's default. */
    fun testLegacyWithoutDefinesAdoptsDefault() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.ensureDefaults()

        val cfg = config()
        cfg.readExternal(Element("configuration"))

        assertEquals(IsBuildConfiguration.DEFAULT_DEBUG_NAME, cfg.buildConfigurationName)
    }

    /** An already migrated configuration is left alone. */
    fun testExplicitBuildConfigurationWins() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Release"))
        val element = Element("configuration").apply {
            setAttribute("buildConfigurationName", "Release")
            setAttribute("compilerDefines", "DEBUG")
        }

        val cfg = config()
        cfg.readExternal(element)

        assertEquals("Release", cfg.buildConfigurationName)
    }

    // ── defaults ─────────────────────────────────────────────────────────────────

    fun testDefaultValues() {
        val cfg = config()
        assertTrue(cfg.debugOutput)
        assertTrue(cfg.languageOverride.isEmpty())
        assertTrue(cfg.persistentTempOutputDir.isEmpty())
    }

    /** A new run configuration starts on `Debug`, so running a script defines `DEBUG` out of the box. */
    fun testTemplateConfigurationSelectsDebug() {
        val factory = IsRunConfigurationType().configurationFactories.first()
        val created = factory.createTemplateConfiguration(project) as IsRunConfiguration

        assertEquals(IsBuildConfiguration.DEFAULT_DEBUG_NAME, created.buildConfigurationName)
        assertEquals(mapOf("DEBUG" to null), created.buildConfiguration()?.symbols)
    }
}
