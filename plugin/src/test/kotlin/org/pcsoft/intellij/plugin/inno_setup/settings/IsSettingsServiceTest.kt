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
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService

/**
 * Tests for the installation detection, the version comparison and the selectable version list.
 */
class IsSettingsServiceTest {

    @Rule
    @JvmField
    val tempDir = TemporaryFolder()

    // ── null-path cases ─────────────────────────────────────────────────────────

    /** A blank path means "no installation configured" and yields no version. */
    @Test
    fun `blank path returns null`() {
        assertNull(IsSettingsService.detectVersionFromPath(""))
    }

    /** A whitespace-only path is treated like a blank path. */
    @Test
    fun `whitespace-only path returns null`() {
        assertNull(IsSettingsService.detectVersionFromPath("   "))
    }

    /** A directory that does not exist yields no version and does not throw. */
    @Test
    fun `non-existent directory returns null`() {
        assertNull(IsSettingsService.detectVersionFromPath(tempDir.root.resolve("does-not-exist").absolutePath))
    }

    // ── missing-executable cases ─────────────────────────────────────────────────

    /** An empty directory contains no compiler and yields no version. */
    @Test
    fun `directory without any exe returns null`() {
        assertNull(IsSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    /**
     * Inno Setup 7 renamed the IDE `Compil32.exe` to `ISIDE.exe`; a directory with only the IDE
     * executable but without `ISCC.exe` is not a usable installation.
     */
    @Test
    fun `directory with ISIDE_exe but without ISCC_exe returns null`() {
        tempDir.newFile("ISIDE.exe")
        assertNull(IsSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    /** A legacy directory with only `Compil32.exe` but without `ISCC.exe` is not usable either. */
    @Test
    fun `directory with Compil32_exe but without ISCC_exe returns null`() {
        tempDir.newFile("Compil32.exe")
        assertNull(IsSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    // ── ISCC present but process fails ───────────────────────────────────────────

    /**
     * A directory with only `ISCC.exe` (as in Inno Setup 7, where no `Compil32.exe` exists) is no longer
     * rejected up front; the dummy file is not a valid executable, so the start fails and null is returned
     * gracefully instead of an exception.
     */
    @Test
    fun `ISCC_exe without Compil32_exe is accepted and invalid executable returns null gracefully`() {
        // Dummy 0-byte file is not a real executable; ProcessBuilder will throw an exception.
        tempDir.newFile("ISCC.exe")
        // Must not throw — exceptions inside detectVersionFromPath are caught and return null.
        assertNull(IsSettingsService.detectVersionFromPath(tempDir.root.absolutePath))
    }

    // ── compareIsVersions ─────────────────────────────────────────────────────────

    /** Identical versions compare as equal. */
    @Test
    fun `same versions are equal`() {
        assertEquals(0, IsSettingsService.compareIsVersions("6.0", "6.0"))
        assertEquals(0, IsSettingsService.compareIsVersions("7.0", "7.0"))
    }

    /** A lower minor version compares as less than a higher one. */
    @Test
    fun `lower minor version is less than higher`() {
        assertTrue(IsSettingsService.compareIsVersions("6.0", "6.7") < 0)
        assertTrue(IsSettingsService.compareIsVersions("6.4", "6.5") < 0)
    }

    /** A higher minor version compares as greater than a lower one. */
    @Test
    fun `higher minor version is greater than lower`() {
        assertTrue(IsSettingsService.compareIsVersions("6.7", "6.0") > 0)
        assertTrue(IsSettingsService.compareIsVersions("6.5", "6.4") > 0)
    }

    /** The major version dominates the minor version. */
    @Test
    fun `higher major version is greater regardless of minor`() {
        assertTrue(IsSettingsService.compareIsVersions("7.0", "6.7") > 0)
        assertTrue(IsSettingsService.compareIsVersions("7.0", "6.0") > 0)
    }

    /** A lower major version compares as less, even with a higher minor version. */
    @Test
    fun `lower major version is less regardless of minor`() {
        assertTrue(IsSettingsService.compareIsVersions("6.7", "7.0") < 0)
    }

    /** Versions consisting of only a major component compare correctly. */
    @Test
    fun `single-component versions compare correctly`() {
        assertTrue(IsSettingsService.compareIsVersions("6", "7") < 0)
        assertEquals(0, IsSettingsService.compareIsVersions("6", "6"))
    }

    /** Three-component Inno Setup 7 versions (7.0, 7.0.1, 7.1) are ordered correctly. */
    @Test
    fun `inno setup 7 patch and minor versions are ordered`() {
        assertTrue(IsSettingsService.compareIsVersions("7.0", "7.0.1") < 0)
        assertTrue(IsSettingsService.compareIsVersions("7.0.1", "7.1") < 0)
        assertTrue(IsSettingsService.compareIsVersions("7.1", "7.0.1") > 0)
        assertEquals(0, IsSettingsService.compareIsVersions("7.0", "7.0.0"))
    }

    // ── selectable version list ───────────────────────────────────────────────────

    /** Inno Setup 7 lists only versions with script or compiler changes: 7.0, 7.0.1 and 7.1. */
    @Test
    fun `version list for major 7 contains only versions with effective changes`() {
        assertEquals(listOf("7.0", "7.0.1", "7.1"), IsSettingsConfigurable.versionsForMajor(7))
    }

    /** The latest selectable version of major 7 is 7.1. */
    @Test
    fun `latest version for major 7 is 7_1`() {
        assertEquals("7.1", IsSettingsConfigurable.latestForMajor(7))
    }

    /** The major version is extracted from saved restrictions with two or three components. */
    @Test
    fun `major is extracted from restrictions with patch component`() {
        assertEquals(7, IsSettingsConfigurable.extractMajorFromRestriction("7.0.1"))
        assertEquals(7, IsSettingsConfigurable.extractMajorFromRestriction("7.1"))
        assertEquals(6, IsSettingsConfigurable.extractMajorFromRestriction("6.4"))
    }

    // ── integration test against a real installation ──────────────────────────────

    /** Detects the major version of a real Inno Setup 6 installation; skipped when it is not installed. */
    @Test
    fun `real Inno Setup installation is detected correctly`() {
        val installDir = java.io.File("C:\\Program Files (x86)\\Inno Setup 6")
        if (!installDir.resolve("ISCC.exe").exists()) {
            // Skip when Inno Setup is not installed on the current machine.
            return
        }
        val version = IsSettingsService.detectVersionFromPath(installDir.absolutePath)
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

    /** Detects the major version of a real Inno Setup 7 installation (no `Compil32.exe`); skipped when absent. */
    @Test
    fun `real Inno Setup 7 installation is detected correctly`() {
        val installDir = java.io.File("C:\\Program Files\\Inno Setup 7")
        if (!installDir.resolve("ISCC.exe").exists()) {
            // Skip when Inno Setup 7 is not installed on the current machine.
            return
        }
        assertEquals("Inno Setup 7", IsSettingsService.detectVersionFromPath(installDir.absolutePath))
    }
}
