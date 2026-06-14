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
        assertTrue(arg!!.startsWith("/O\""))
        assertEquals(File(buildRoot, "Output").path, arg.removePrefix("/O").removeSurrounding("\""))
    }

    @Test
    fun `build dir defaults to Output when script has none`() {
        val buildRoot = File(tempDir.root, "build")
        val arg = IsBuildOutputResolver.computeOutputArg(IsBuildOutputMode.BUILD_DIR, null, buildRoot)
        assertEquals(File(buildRoot, "Output").path, arg!!.removePrefix("/O").removeSurrounding("\""))
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
