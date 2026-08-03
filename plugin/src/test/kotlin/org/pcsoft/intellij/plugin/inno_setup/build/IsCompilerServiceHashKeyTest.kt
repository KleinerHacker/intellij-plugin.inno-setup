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

import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedTestCase
import java.io.File

/**
 * Developer tests for the build's up-to-date identity ([IsCompilerService.hashKey]).
 *
 * This is the decision that determines whether an existing installer is reused, so every way a build
 * configuration can change the produced installer must produce a different key — and every change that
 * cannot must produce the same one, or a plain re-run would recompile for nothing.
 */
class IsCompilerServiceHashKeyTest : IsTimedTestCase() {

    // Derived from a real directory rather than hard-coded as "C:\proj\…": the canonicalisation case below
    // depends on the OS actually treating the separators as separators, and on Linux a backslash is an
    // ordinary filename character — which made that test fail on the CI runner while passing locally. The
    // directory need not exist; File.canonicalPath resolves "…/sub/.." purely textually for a missing path.
    private val projectDir = File(System.getProperty("java.io.tmpdir"), "inno-setup-hash-key-test")
    private val script = File(projectDir, "setup.iss").path
    private val output = "/O\"" + File(projectDir, "out").path + "\""

    private fun key(config: IsBuildConfiguration?, path: String = script, outputArg: String? = output) =
        IsCompilerService.hashKey(path, outputArg, config)

    // ── same identity: the installer stays valid ────────────────────────────────

    /** Two runs with the same script, output and configuration content share one key. */
    fun testIdenticalInputsShareKey() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        assertEquals(key(debug), key(IsBuildConfiguration("Debug", "DEBUG")))
    }

    /** Renaming the build configuration alone must not invalidate the previous build. */
    fun testRenameKeepsKey() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        assertEquals(key(debug), key(debug.copy(name = "Development")))
    }

    /** Re-spelling the same symbols must not invalidate it either. */
    fun testResspelledSymbolsKeepKey() {
        assertEquals(
            key(IsBuildConfiguration("c", "DEBUG,VERSION=2")),
            key(IsBuildConfiguration("c", "DEBUG VERSION = 2"))
        )
    }

    // ── different identity: the installer must be rebuilt ───────────────────────

    /** Editing a configuration's symbols invalidates the installer built from the old ones. */
    fun testChangedSymbolsChangeKey() {
        assertFalse(key(IsBuildConfiguration("Debug")) == key(IsBuildConfiguration("Debug", "DEBUG")))
    }

    /** Switching a run from `Debug` to `Release` must never reuse the other one's installer. */
    fun testSwitchingBetweenDefaultsChangesKey() {
        val (release, debug) = IsBuildConfiguration.defaults()
        assertFalse(key(release) == key(debug))
    }

    /** Switching back to a configuration returns to its own key, so its installer is reused. */
    fun testSwitchingBackRestoresKey() {
        val (release, debug) = IsBuildConfiguration.defaults()
        val first = key(debug)
        key(release)
        assertEquals(first, key(debug))
    }

    /** A changed output directory override belongs to a different build. */
    fun testChangedOutputOverrideChangesKey() {
        assertFalse(
            key(IsBuildConfiguration("c", outputDirOverride = "dist/a")) ==
                    key(IsBuildConfiguration("c", outputDirOverride = "dist/b"))
        )
    }

    /** Changed extra ISCC options change the compiler invocation and therefore the key. */
    fun testChangedOptionsChangeKey() {
        assertFalse(
            key(IsBuildConfiguration("c", additionalIsccOptions = "/Q")) ==
                    key(IsBuildConfiguration("c", additionalIsccOptions = "/Q /Sname=sign"))
        )
    }

    /** Running without any configuration is its own identity, distinct from every configuration. */
    fun testNoConfigurationDiffersFromAnyConfiguration() {
        assertFalse(key(null) == key(IsBuildConfiguration("Release")))
    }

    /** An empty configuration still differs from none, so a plain build and a run do not collide. */
    fun testEmptyConfigurationDiffersFromNone() {
        assertFalse(key(null) == key(IsBuildConfiguration("Empty")))
    }

    // ── the pre-existing parts of the identity still hold ───────────────────────

    /** Different scripts keep separate keys even with the same configuration. */
    fun testDifferentScriptsDiffer() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        assertFalse(key(debug) == key(debug, path = File(projectDir, "other.iss").path))
    }

    /** Different output arguments keep separate keys, so a build and a run never skip each other. */
    fun testDifferentOutputArgsDiffer() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        assertFalse(key(debug) == key(debug, outputArg = "/O-"))
    }

    /** The same script spelled differently is one script, so its key must not change. */
    fun testPathIsCanonicalised() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        val detour = File(projectDir, "sub${File.separator}..${File.separator}setup.iss").path
        assertEquals(key(debug), key(debug, path = detour))
    }
}
