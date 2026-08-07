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

    @get:Rule
    val tmp = TemporaryFolder()

    // ── buildOutputArg ────────────────────────────────────────────────────────

    @Test
    fun `DRY mode overrides to temp directory`() {
        val buildRoot = tmp.newFolder("build")
        val tempBase = tmp.newFolder("temp")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, null, tempBase)
        assertTrue("must be under tempBase", arg.startsWith("/O${tempBase.path}"))
        assertFalse("the path must not be quoted — GeneralCommandLine quotes it", arg.contains('"'))
    }

    @Test
    fun `DRY mode generates unique directories`() {
        val buildRoot = tmp.newFolder("build")
        val tempBase = tmp.newFolder("temp")
        val arg1 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, null, tempBase)
        val arg2 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, null, tempBase)
        assertNotEquals(arg1, arg2)
    }

    @Test
    fun `DRY mode reuses persistent temp directory when provided`() {
        val buildRoot = tmp.newFolder("build")
        val tempBase = tmp.newFolder("temp")
        val persistent = tmp.newFolder("persistent").path
        val arg1 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, persistent, tempBase)
        val arg2 = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.DRY, buildRoot, null, persistent, tempBase)
        assertEquals("/O$persistent", arg1)
        assertEquals(arg1, arg2)
    }

    @Test
    fun `BUILD_DIR mode uses buildRoot with relative scriptOutputDir`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.BUILD_DIR, buildRoot, "Release")
        assertEquals("/O" + File(buildRoot, "Release").path, arg)
    }

    @Test
    fun `BUILD_DIR mode falls back to Output when scriptOutputDir is null`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.BUILD_DIR, buildRoot, null)
        assertEquals("/O" + File(buildRoot, "Output").path, arg)
    }

    @Test
    fun `SCRIPT mode uses buildRoot with scriptOutputDir`() {
        val buildRoot = tmp.newFolder("build")
        val arg = IsScriptRunnerLogic.buildOutputArg(IsBuildOutputMode.SCRIPT, buildRoot, "Dist")
        assertEquals("/O" + File(buildRoot, "Dist").path, arg)
    }

    // ── buildInstallerArgs ────────────────────────────────────────────────────

    @Test
    fun `no flags produce just the exe path`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, null, false, null)
        assertEquals(listOf(exe.absolutePath), args)
    }

    @Test
    fun `language override is appended`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, "German", false, null)
        assertTrue(args.any { it == "/LANG=German" })
    }

    @Test
    fun `blank language override is ignored`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, "  ", false, null)
        assertFalse(args.any { it.startsWith("/LANG") })
    }

    @Test
    fun `debug output adds LOG flag`() {
        val exe = File("setup.exe")
        val log = tmp.newFile("out.log")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, null, true, log)
        assertTrue(args.any { it.startsWith("/LOG=") })
    }

    @Test
    fun `debug output without logFile does not add LOG`() {
        val exe = File("setup.exe")
        val args = IsScriptRunnerLogic.buildInstallerArgs(exe, null, true, null)
        assertFalse(args.any { it.startsWith("/LOG") })
    }

    // ── findSetupExe ──────────────────────────────────────────────────────────

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

    // ── outputDirFromArg ──────────────────────────────────────────────────────

    @Test
    fun `outputDirFromArg extracts path`() {
        val result = IsScriptRunnerLogic.outputDirFromArg("/OC:\\temp\\out")
        assertEquals(File("C:\\temp\\out"), result)
    }

    @Test
    fun `outputDirFromArg strips surrounding quotes`() {
        val result = IsScriptRunnerLogic.outputDirFromArg("/O\"C:\\temp\\my out\"")
        assertEquals(File("C:\\temp\\my out"), result)
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

    // ── build configuration output override ──────────────────────────────────────
    //
    // These cases turn on the difference between a relative and an absolute path, which only the running OS
    // can decide: on Linux "C:\out" is neither absolute nor even a nested path — the backslash is an ordinary
    // filename character — so a hard-coded Windows path made every one of these tests fail on the CI runner
    // while passing locally. Paths are therefore derived from the TemporaryFolder rule, which yields a real
    // absolute path with native separators on whatever OS the suite runs on.

    /** A build configuration that sets no override leaves the resolved argument untouched. */
    @Test
    fun `applyOutputOverride keeps base argument when override is blank`() {
        val buildRoot = tmp.newFolder("build")
        assertEquals("/O-", IsScriptRunnerLogic.applyOutputOverride("/O-", "", buildRoot))
        assertEquals("/O-", IsScriptRunnerLogic.applyOutputOverride("/O-", null, buildRoot))
        assertEquals("/O-", IsScriptRunnerLogic.applyOutputOverride("/O-", "   ", buildRoot))
    }

    /** A relative override is resolved below the project build directory, not the working directory. */
    @Test
    fun `applyOutputOverride resolves relative path against build root`() {
        val buildRoot = tmp.newFolder("build")
        val relative = "dist${File.separator}debug"
        assertEquals(
            IsScriptRunnerLogic.outputArg(File(buildRoot, relative).path),
            IsScriptRunnerLogic.applyOutputOverride("/O-", relative, buildRoot)
        )
    }

    /** An absolute override is taken as given. */
    @Test
    fun `applyOutputOverride keeps absolute path`() {
        val absolute = tmp.newFolder("out").path
        val result = IsScriptRunnerLogic.applyOutputOverride("/O-", absolute, tmp.newFolder("build"))
        assertEquals(IsScriptRunnerLogic.outputArg(absolute), result)
    }

    /** An already quoted override is not quoted twice. */
    @Test
    fun `applyOutputOverride strips existing quotes`() {
        // A space in the name is the reason the caller quotes at all, so keep one here.
        val absolute = tmp.newFolder("my out").path
        val result = IsScriptRunnerLogic.applyOutputOverride("/O-", "\"$absolute\"", tmp.newFolder("build"))
        assertEquals(IsScriptRunnerLogic.outputArg(absolute), result)
    }

    /** A null base argument stays null when there is nothing to override it with. */
    @Test
    fun `applyOutputOverride keeps null base without override`() {
        assertNull(IsScriptRunnerLogic.applyOutputOverride(null, "", tmp.newFolder("build")))
    }

    /** An override also supplies an argument where the project rule produced none. */
    @Test
    fun `applyOutputOverride replaces missing base argument`() {
        val absolute = tmp.newFolder("out").path
        val result = IsScriptRunnerLogic.applyOutputOverride(null, absolute, tmp.newFolder("build"))
        assertEquals(IsScriptRunnerLogic.outputArg(absolute), result)
    }
}
