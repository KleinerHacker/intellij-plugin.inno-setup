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

package org.pcsoft.intellij.plugin.inno_setup.language.feature

import com.intellij.execution.RunManager
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationType
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Tests that the ISCC `/D<name>[=<value>]` symbols of the selected run configuration's build configuration are
 * treated as defined everywhere — in an inline `{#Name}` emission as well as in an ISPP expression — instead of
 * being reported as an unknown constant resp. an unresolved reference.
 */
class IsPreprocessorExternalSymbolTest : IsTimedBasePlatformTestCase() {

    private lateinit var configDirectory: File
    private lateinit var script: VirtualFile

    override fun setUp() {
        super.setUp()
        configDirectory = File.createTempFile("inno-external-symbols", "").let { probe ->
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

    /** The error descriptions of [content] that contain [fragment]. */
    private fun errorsContaining(content: String, fragment: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.doHighlighting()
            .filter { it.severity == HighlightSeverity.ERROR }
            .mapNotNull { it.description }
            .filter { it.contains(fragment, ignoreCase = true) }
    }

    /**
     * Without a selected run configuration nothing defines `DEBUG`, so emitting it stays an error — the fix
     * must not silently accept every unknown name.
     */
    fun testUnknownSymbolStillProducesAnError() {
        val errors = errorsContaining("[Setup]\nAppName={#DEBUG}\n", "Unknown constant")
        assertFalse("Without a build configuration {#DEBUG} must stay an error", errors.isEmpty())
    }

    /** A `/D` symbol of the selected build configuration is emittable via `{#Name}` without an error. */
    fun testExternalSymbolIsEmittable() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("run debug", "Debug")

        val errors = errorsContaining("[Setup]\nAppName={#DEBUG}\n", "Unknown constant")
        assertTrue("A /D symbol must be a valid inline emission, but got: $errors", errors.isEmpty())
    }

    /** ISPP compares symbol names case-insensitively, so `{#debug}` must resolve to `/DDEBUG` as well. */
    fun testExternalSymbolIsCaseInsensitive() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "DEBUG"))
        select("run debug", "Debug")

        val errors = errorsContaining("[Setup]\nAppName={#debug}\n", "Unknown constant")
        assertTrue("A /D symbol must match case-insensitively, but got: $errors", errors.isEmpty())
    }

    /** A `/D` symbol used in an ISPP expression is a defined name, not an unresolved reference. */
    fun testExternalSymbolResolvesInDefineExpression() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "VERSION=2"))
        select("run debug", "Debug")

        val errors = errorsContaining("#define Build VERSION + 1\n[Setup]\nAppName=T\n", "Unresolved")
        assertTrue("A /D symbol must resolve inside a #define, but got: $errors", errors.isEmpty())
    }

    /** The same holds inside a condition, which is where build flags are used most. */
    fun testExternalSymbolResolvesInCondition() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "VERSION=2"))
        select("run debug", "Debug")

        val errors = errorsContaining(
            "#if VERSION > 1\n#define X 1\n#endif\n[Setup]\nAppName=T\n",
            "Unresolved",
        )
        assertTrue("A /D symbol must resolve inside a condition, but got: $errors", errors.isEmpty())
    }

    /** A `/D` symbol carrying a numeric value is typed as an integer, so mixing it with a string is flagged. */
    fun testNumericExternalSymbolIsTypedAsInt() {
        IsBuildConfigurationService.getInstance(project).save(IsBuildConfiguration("Debug", "VERSION=2"))
        select("run debug", "Debug")

        val errors = errorsContaining(
            "#define X VERSION * \"a\"\n[Setup]\nAppName=T\n",
            "cannot be applied to a string operand",
        )
        assertFalse("An int-valued /D symbol must take part in the type check", errors.isEmpty())
    }

    /** Switching the selected run configuration switches the symbols instead of serving a stale cache. */
    fun testSwitchingRunConfigurationInvalidatesTheSymbols() {
        val service = IsBuildConfigurationService.getInstance(project)
        service.save(IsBuildConfiguration("Debug", "DEBUG"))
        service.save(IsBuildConfiguration("Release", "RELEASE"))
        select("run debug", "Debug")
        assertTrue(errorsContaining("[Setup]\nAppName={#DEBUG}\n", "Unknown constant").isEmpty())

        select("run release", "Release")

        assertFalse(
            "After switching to Release the Debug symbol must be unknown again",
            errorsContaining("[Setup]\nAppName={#DEBUG}\n", "Unknown constant").isEmpty(),
        )
        assertTrue(
            "After switching to Release its own symbol must be known",
            errorsContaining("[Setup]\nAppName={#RELEASE}\n", "Unknown constant").isEmpty(),
        )
    }
}
