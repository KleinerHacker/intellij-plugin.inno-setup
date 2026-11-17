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

package org.pcsoft.intellij.plugin.inno_setup.build

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Tests the pure path-mapping helpers of [IsBuildOutputResolver] (no project/PSI involved).
 */
class IsBuildOutputResolverHelperTest {

    @Rule
    @JvmField
    val tempDir = TemporaryFolder()

    // ── computeOutputArg ─────────────────────────────────────────────────────────

    @Test
    fun `dry mode disables output`() {
        assertEquals("/O-", IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.DRY, "Output", File("build")))
    }

    @Test
    fun `script mode never overrides`() {
        assertNull(IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.SCRIPT, "Output", File("build")))
    }

    @Test
    fun `build dir maps a relative output dir below the build root`() {
        val buildRoot = File(tempDir.root, "out")
        val arg = IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.BUILD_DIR, "Output", buildRoot)
        assertNotNull(arg)
        // Unquoted: GeneralCommandLine performs the Windows quoting of the parameter itself.
        assertEquals("/O" + File(buildRoot, "Output").path, arg)
    }

    @Test
    fun `build dir defaults to Output when script has none`() {
        val buildRoot = File(tempDir.root, "build")
        val arg = IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.BUILD_DIR, null, buildRoot)
        assertEquals("/O" + File(buildRoot, "Output").path, arg)
    }

    @Test
    fun `build dir leaves an absolute output dir untouched`() {
        val absolute = File(tempDir.root, "dist").absolutePath
        assertNull(IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.BUILD_DIR, absolute, File("out")))
    }

    // ── parseOutputDir ───────────────────────────────────────────────────────────

    @Test
    fun `parses relative output dir`() {
        assertEquals("Release", IsBuildOutputResolver.parseOutputDir("[Setup]\nOutputDir=Release\nAppName=x"))
    }

    @Test
    fun `parses absolute windows path with drive colon`() {
        assertEquals("C:\\dist", IsBuildOutputResolver.parseOutputDir("[Setup]\nOutputDir=C:\\dist\n"))
    }

    @Test
    fun `strips surrounding quotes and is case-insensitive`() {
        assertEquals("My Out", IsBuildOutputResolver.parseOutputDir("outputdir=\"My Out\""))
    }

    @Test
    fun `returns null when absent`() {
        assertNull(IsBuildOutputResolver.parseOutputDir("[Setup]\nAppName=x"))
    }

    // ── expandOutputDir ──────────────────────────────────────────────────────────

    /**
     * Use case of issue #60: `OutputDir={#OutputDirectoryPath}` must be expanded with the value of the
     * `#define` instead of being treated as a literal relative folder name.
     */
    @Test
    fun `expands a preprocessor define`() {
        val defines = listOf("OutputDirectoryPath" to "..\\artifacts\\setup")
        assertEquals(
            "..\\artifacts\\setup",
            IsBuildOutputResolver.expandOutputDir("{#OutputDirectoryPath}", defines, null, null)
        )
    }

    /**
     * A define whose name differs only in case is still found, because ISPP symbol names are case-insensitive.
     */
    @Test
    fun `expands a define case-insensitively and keeps surrounding text`() {
        val defines = listOf("Dist" to "out")
        assertEquals("out\\x64", IsBuildOutputResolver.expandOutputDir("{#dist}\\x64", defines, null, null))
    }

    /**
     * A value without any placeholder is returned unchanged, so plain relative and absolute paths keep working.
     */
    @Test
    fun `keeps a plain value unchanged`() {
        assertEquals("Release", IsBuildOutputResolver.expandOutputDir("Release", emptyList(), null, null))
    }

    /**
     * The `{src}` constant is expanded against the script directory.
     */
    @Test
    fun `expands the src constant against the script directory`() {
        val scriptDir = tempDir.root
        val expanded = IsBuildOutputResolver.expandOutputDir("{src}\\dist", emptyList(), scriptDir, null)
        assertNotNull(expanded)
        assertTrue("was: $expanded", expanded!!.startsWith(scriptDir.path) && expanded.endsWith("\\dist"))
    }

    /**
     * A define that does not exist cannot be expanded statically; the result is `null` so that no `/O` override
     * is built from the unexpanded text.
     */
    @Test
    fun `returns null for an unknown define`() {
        assertNull(IsBuildOutputResolver.expandOutputDir("{#Missing}", emptyList(), null, null))
    }

    /**
     * A define without a value (function macro) cannot be expanded statically either.
     */
    @Test
    fun `returns null for a define without value`() {
        assertNull(IsBuildOutputResolver.expandOutputDir("{#Macro}", listOf("Macro" to null), null, null))
    }

    // ── detectBuildSubdir ────────────────────────────────────────────────────────

    @Test
    fun `maven project uses target`() {
        tempDir.newFile("pom.xml")
        assertEquals("target", IsBuildOutputResolver.detectBuildSubdir(tempDir.root))
    }

    @Test
    fun `gradle kotlin project uses build`() {
        tempDir.newFile("build.gradle.kts")
        assertEquals("build", IsBuildOutputResolver.detectBuildSubdir(tempDir.root))
    }

    @Test
    fun `gradle groovy project uses build`() {
        tempDir.newFile("build.gradle")
        assertEquals("build", IsBuildOutputResolver.detectBuildSubdir(tempDir.root))
    }

    @Test
    fun `plain project falls back to out`() {
        assertEquals("out", IsBuildOutputResolver.detectBuildSubdir(tempDir.root))
    }
}
