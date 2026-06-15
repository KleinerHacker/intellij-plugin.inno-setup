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

import com.intellij.openapi.util.SystemInfo
import junit.framework.TestCase
import java.io.File
import java.nio.file.Files

class IsMessagesFileResolverTest : TestCase() {

    // ── expandValue ───────────────────────────────────────────────────────────

    fun testExpandValueNoPlaceholders() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "Languages\\German.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertEquals("Languages\\German.isl", result)
    }

    fun testExpandValueIsppKnown() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "Languages\\{#LangFile}.isl",
            defines = listOf("LangFile" to "German"),
            scriptDir = null
        )
        assertEquals("Languages\\German.isl", result)
    }

    fun testExpandValueIsppUnknown() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#Unknown}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertNull("Unknown ISPP define must yield null", result)
    }

    fun testExpandValueIsppNullValue() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#Macro}.isl",
            defines = listOf("Macro" to null),
            scriptDir = null
        )
        assertNull("ISPP define with null value (function macro) must yield null", result)
    }

    fun testExpandValueEnvKnown() {
        val path = System.getenv("PATH") ?: return  // skip if PATH not set (unlikely)
        val result = IsMessagesFileResolver.expandValue(
            raw = "{%PATH}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertEquals("$path\\lang.isl", result)
    }

    fun testExpandValueEnvUnknownWithDefault() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{%GIBTSNICHT_XYZ123|fallback}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertEquals("fallback\\lang.isl", result)
    }

    fun testExpandValueEnvUnknownNoDefault() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{%GIBTSNICHT_XYZ123}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertNull("Unknown env var without default must yield null", result)
    }

    fun testExpandValueSrcConstant() {
        val dir = File(System.getProperty("java.io.tmpdir"))
        val result = IsMessagesFileResolver.expandValue(
            raw = "{src}\\lang.isl",
            defines = emptyList(),
            scriptDir = dir
        )
        assertEquals("${dir.absolutePath}\\lang.isl", result)
    }

    // ── expandValue: ISPP predefined variables (value-bearing, via {#…}) ──────────

    fun testExpandValuePredefinedSourcePath() {
        val dir = File(System.getProperty("java.io.tmpdir"))
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#SourcePath}\\lang.isl",
            defines = emptyList(),
            scriptDir = dir
        )
        assertEquals("${dir.absolutePath}\\lang.isl", result)
    }

    fun testExpandValuePredefinedDir() {
        val dir = File(System.getProperty("java.io.tmpdir"))
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#__DIR__}\\lang.isl",
            defines = emptyList(),
            scriptDir = dir
        )
        assertEquals("${dir.absolutePath}\\lang.isl", result)
    }

    fun testExpandValuePredefinedCompilerPath() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#CompilerPath}\\Languages\\German.isl",
            defines = emptyList(),
            scriptDir = null,
            installPath = "C:\\Inno"
        )
        assertEquals("C:\\Inno\\Languages\\German.isl", result)
    }

    fun testExpandValuePredefinedCompilerPathNotConfigured() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#CompilerPath}\\Languages\\German.isl",
            defines = emptyList(),
            scriptDir = null,
            installPath = null
        )
        assertNull("Without an install path, {#CompilerPath} must stay unresolvable", result)
    }

    fun testExpandValuePredefinedDynamicYieldsNull() {
        // Compile-time-only / non-path predefined variables have no statically known value.
        assertNull(
            "{#__LINE__} must stay unresolvable",
            IsMessagesFileResolver.expandValue("{#__LINE__}.isl", emptyList(), null)
        )
        assertNull(
            "{#NewLine} must stay unresolvable",
            IsMessagesFileResolver.expandValue("{#NewLine}lang.isl", emptyList(), null)
        )
    }

    fun testExpandValueDefineTakesPrecedenceOverPredefined() {
        // A user #define with the same name as a predefined variable wins.
        val result = IsMessagesFileResolver.expandValue(
            raw = "{#SourcePath}\\lang.isl",
            defines = listOf("SourcePath" to "Custom"),
            scriptDir = File(System.getProperty("java.io.tmpdir"))
        )
        assertEquals("Custom\\lang.isl", result)
    }

    fun testExpandValueInstallTimeConstant() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{app}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        assertNull("{app} is install-time only and must yield null", result)
    }

    fun testExpandValueWinConstant() {
        val result = IsMessagesFileResolver.expandValue(
            raw = "{win}\\lang.isl",
            defines = emptyList(),
            scriptDir = null
        )
        if (SystemInfo.isWindows) {
            assertNotNull("On Windows, {win} should resolve", result)
        } else {
            assertNull("On non-Windows, {win} must yield null", result)
        }
    }

    // ── resolveMessagesFile ───────────────────────────────────────────────────

    fun testResolveCompilerPrefixNotConfigured() {
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = "compiler:Languages\\German.isl",
            scriptDir = null,
            installPath = null
        )
        assertEquals(IsResolveResult.NotConfigured, result)
    }

    fun testResolveCompilerPrefixBlankInstallPath() {
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = "compiler:Languages\\German.isl",
            scriptDir = null,
            installPath = "   "
        )
        assertEquals(IsResolveResult.NotConfigured, result)
    }

    fun testResolveCompilerPrefixMissing() {
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = "compiler:Languages\\NonExistent.isl",
            scriptDir = File(System.getProperty("java.io.tmpdir")),
            installPath = "C:\\FakeInnoPath"
        )
        assertTrue(result is IsResolveResult.Missing)
    }

    fun testResolveAbsoluteMissing() {
        val nonExistent = File(System.getProperty("java.io.tmpdir"), "does_not_exist_xyz.isl")
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = nonExistent.absolutePath,
            scriptDir = null,
            installPath = null
        )
        assertTrue(result is IsResolveResult.Missing)
    }

    fun testResolveAbsoluteOk() {
        val tmp = Files.createTempFile("test_isl_", ".isl").toFile()
        try {
            val result = IsMessagesFileResolver.resolveMessagesFile(
                expandedValue = tmp.absolutePath,
                scriptDir = null,
                installPath = null
            )
            assertTrue("Existing readable file must yield Ok", result is IsResolveResult.Ok)
        } finally {
            tmp.delete()
        }
    }

    fun testResolveRelativeOk() {
        val tmp = Files.createTempFile("test_isl_", ".isl").toFile()
        val scriptDir = tmp.parentFile
        try {
            val result = IsMessagesFileResolver.resolveMessagesFile(
                expandedValue = tmp.name,
                scriptDir = scriptDir,
                installPath = null
            )
            assertTrue("Relative path to existing file must yield Ok", result is IsResolveResult.Ok)
        } finally {
            tmp.delete()
        }
    }

    fun testResolveRelativeMissing() {
        val scriptDir = File(System.getProperty("java.io.tmpdir"))
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = "does_not_exist_xyz.isl",
            scriptDir = scriptDir,
            installPath = null
        )
        assertTrue(result is IsResolveResult.Missing)
    }

    fun testResolveNoScriptDirRelative() {
        val result = IsMessagesFileResolver.resolveMessagesFile(
            expandedValue = "Languages\\German.isl",
            scriptDir = null,
            installPath = null
        )
        // Without scriptDir, relative path is unresolvable
        assertEquals(IsResolveResult.Unresolvable, result)
    }
}
