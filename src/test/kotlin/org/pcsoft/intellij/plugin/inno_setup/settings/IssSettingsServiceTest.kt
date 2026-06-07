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

package org.pcsoft.intellij.plugin.inno_setup.settings

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class IssSettingsServiceTest {

    @Rule
    @JvmField
    val tempDir = TemporaryFolder()

    // ── null-path cases ─────────────────────────────────────────────────────────

    @Test
    fun `blank path returns null`() {
        assertNull(IssSettingsService.detectVersionFromPath(""))
    }

    @Test
    fun `whitespace-only path returns null`() {
        assertNull(IssSettingsService.detectVersionFromPath("   "))
    }

    @Test
    fun `non-existent directory returns null`() {
        assertNull(IssSettingsService.detectVersionFromPath(tempDir.root.resolve("does-not-exist").absolutePath))
    }

    // ── missing-executable cases ─────────────────────────────────────────────────

    @Test
    fun `directory without any exe returns null`() {
        assertNull(IssSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    @Test
    fun `directory with ISCC_exe but without Compil32_exe returns null`() {
        tempDir.newFile("ISCC.exe")
        assertNull(IssSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    @Test
    fun `directory with Compil32_exe but without ISCC_exe returns null`() {
        tempDir.newFile("Compil32.exe")
        assertNull(IssSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    // ── both exes present but process fails ──────────────────────────────────────

    @Test
    fun `both exes present but not a valid executable returns null gracefully`() {
        // Dummy 0-byte files are not real executables; ProcessBuilder will throw an exception.
        tempDir.newFile("ISCC.exe")
        tempDir.newFile("Compil32.exe")
        // Must not throw — exceptions inside detectVersionFromPath are caught and return null.
        assertNull(IssSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    // ── integration test against a real installation ──────────────────────────────

    @Test
    fun `real Inno Setup installation is detected correctly`() {
        val installDir = java.io.File("C:\\Program Files (x86)\\Inno Setup 6")
        if (!installDir.resolve("ISCC.exe").exists() || !installDir.resolve("Compil32.exe").exists()) {
            // Skip when Inno Setup is not installed on the current machine.
            return
        }
        val version = IssSettingsService.detectVersionFromPath(installDir.absolutePath)
        assertNotNull("Expected a version string for a real installation", version)
        assertTrue(
            "Version string should start with 'Inno Setup'",
            version!!.startsWith("Inno Setup")
        )
        assertTrue(
            "Version string should contain a major version number",
            version.matches(Regex("Inno Setup \\d+"))
        )
    }
}
