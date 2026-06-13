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

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import java.io.File

class IsScriptRunnerLogicTest {

    @get:Rule val tmp = TemporaryFolder()

    // ── buildOutputArg ────────────────────────────────────────────────────────

    @Test
    fun `DRY mode overrides to temp directory`() {
        val buildRoot = tmp.newFolder("build")
        val tempBase = tmp.newFolder("temp")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, tempBase)
        assertTrue(arg.startsWith("/O"))
        assertTrue("must be under tempBase", arg.startsWith("/O${tempBase.path}"))
    }

    @Test
    fun `DRY mode generates unique directories`() {
        val buildRoot = tmp.newFolder("build")
        val tempBase = tmp.newFolder("temp")
        val arg1 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, tempBase)
        val arg2 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, tempBase)
        assertNotEquals(arg1, arg2)
    }

    @Test
    fun `BUILD_DIR mode uses buildRoot with relative scriptOutputDir`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.BUILD_DIR, buildRoot, "Release")
        assertTrue(arg.startsWith("/O"))
        assertTrue(arg.endsWith("Release"))
    }

    @Test
    fun `BUILD_DIR mode falls back to Output when scriptOutputDir is null`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.BUILD_DIR, buildRoot, null)
        assertTrue(arg.endsWith("Output"))
    }

    @Test
    fun `SCRIPT mode uses buildRoot with scriptOutputDir`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.SCRIPT, buildRoot, "Dist")
        assertTrue(arg.endsWith("Dist"))
    }

    // ── buildInstallerArgs ────────────────────────────────────────────────────

    @Test
    fun `DRY run adds silent flags`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.DRY, null, false, null)
        assertTrue(args.contains("/VERYSILENT"))
        assertTrue(args.contains("/SUPPRESSMSGBOXES"))
        assertTrue(args.contains("/NORESTART"))
    }

    @Test
    fun `REAL run has no extra flags`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.REAL, null, false, null)
        assertEquals(listOf(exe.absolutePath), args)
    }

    @Test
    fun `language override is appended`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.DRY, "German", false, null)
        assertTrue(args.any { it == "/LANG=German" })
    }

    @Test
    fun `blank language override is ignored`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.DRY, "  ", false, null)
        assertFalse(args.any { it.startsWith("/LANG") })
    }

    @Test
    fun `debug output adds LOG flag`() {
        val exe = File("setup.exe")
        val log = tmp.newFile("out.log")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.DRY, null, true, log)
        assertTrue(args.any { it.startsWith("/LOG=") })
    }

    @Test
    fun `debug output without logFile does not add LOG`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, IsRunMode.DRY, null, true, null)
        assertFalse(args.any { it.startsWith("/LOG") })
    }

    // ── findSetupExe / findUninstallerExe ────────────────────────────────────

    @Test
    fun `findSetupExe returns non-unins exe`() {
        val dir = tmp.newFolder("out")
        File(dir, "mysetup.exe").createNewFile()
        assertEquals("mysetup.exe", IsScriptRunnerLogic.findSetupExe(dir)?.name)
    }

    @Test
    fun `findSetupExe skips uninstaller`() {
        val dir = tmp.newFolder("out")
        File(dir, "unins000.exe").createNewFile()
        assertNull(IsScriptRunnerLogic.findSetupExe(dir))
    }

    @Test
    fun `findUninstallerExe finds unins file`() {
        val dir = tmp.newFolder("inst")
        File(dir, "unins000.exe").createNewFile()
        assertEquals("unins000.exe", IsScriptRunnerLogic.findUninstallerExe(dir)?.name)
    }

    @Test
    fun `findUninstallerExe ignores regular exe`() {
        val dir = tmp.newFolder("inst")
        File(dir, "myapp.exe").createNewFile()
        assertNull(IsScriptRunnerLogic.findUninstallerExe(dir))
    }

    // ── outputDirFromArg ──────────────────────────────────────────────────────

    @Test
    fun `outputDirFromArg extracts path`() {
        val result = IsScriptRunnerLogic.outputDirFromArg("/OC:\\temp\\out")
        assertEquals(File("C:\\temp\\out"), result)
    }

    @Test
    fun `outputDirFromArg returns null for unknown prefix`() {
        assertNull(IsScriptRunnerLogic.outputDirFromArg("/Q"))
        assertNull(IsScriptRunnerLogic.outputDirFromArg(""))
    }

    @Test
    fun `outputDirFromArg parses O- as dash path`() {
        // "/O-" is the disable-output flag; the path extracted is "-" (a relative path).
        // The caller must check for this; outputDirFromArg is a dumb extractor.
        assertEquals(File("-"), IsScriptRunnerLogic.outputDirFromArg("/O-"))
    }
}
