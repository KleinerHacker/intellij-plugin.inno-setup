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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for the version-aware ISPP checks (directive lines and inline `{#…}` emissions), for the directive
 * shorthands and for the Inno Setup 7 entries of the ISPP specification.
 */
class IsPreprocessorVersionAnnotatorTest : IsTimedBasePlatformTestCase() {

    private fun withMinVersion(version: String?, block: () -> Unit) {
        val state = IsSettingsService.getInstance().state
        val previous = state.minInnoVersion
        state.minInnoVersion = version
        try {
            block()
        } finally {
            state.minInnoVersion = previous
        }
    }

    private fun highlights(content: String) =
        myFixture.run { configureByText(IsScriptFileType.INSTANCE, content); doHighlighting() }

    private fun versionWarnings(content: String) =
        highlights(content).filter {
            it.severity == HighlightSeverity.WARNING && it.description?.contains("requires Inno Setup") == true
        }

    /** `Format` exists since Inno Setup 7.1; used in a `#define` with minimum 7.0 it must produce a warning. */
    fun testFunctionNewerThanMinimumInDirectiveProducesWarning() {
        withMinVersion("7.0") {
            val warnings = versionWarnings("#define Text Format(\"%d\", 1)\n[Setup]\nAppName=Test\n")
            assertTrue("'Format' (since 7.1) with minimum 7.0 must produce a version WARNING", warnings.isNotEmpty())
        }
    }

    /** With minimum 7.1 the `Format` function is available and must not be reported. */
    fun testFunctionSatisfyingMinimumInDirectiveProducesNoWarning() {
        withMinVersion("7.1") {
            val warnings = versionWarnings("#define Text Format(\"%d\", 1)\n[Setup]\nAppName=Test\n")
            assertTrue("'Format' (since 7.1) with minimum 7.1 must not produce a version WARNING", warnings.isEmpty())
        }
    }

    /** The predefined variable `SysPath` exists since 7.0.1; inline in a script value with minimum 7.0 it is reported. */
    fun testVariableNewerThanMinimumInInlineEmissionProducesWarning() {
        withMinVersion("7.0") {
            val warnings = versionWarnings("[Setup]\nAppName={#SysPath}\n")
            assertTrue("'SysPath' (since 7.0.1) with minimum 7.0 must produce a version WARNING", warnings.isNotEmpty())
        }
    }

    /** With minimum 7.0.1 the inline `SysPath` variable is available and must not be reported. */
    fun testVariableSatisfyingMinimumInInlineEmissionProducesNoWarning() {
        withMinVersion("7.0.1") {
            val warnings = versionWarnings("[Setup]\nAppName={#SysPath}\n")
            assertTrue("'SysPath' (since 7.0.1) with minimum 7.0.1 must not produce a version WARNING", warnings.isEmpty())
        }
    }

    /** Without a configured minimum version nothing is checked at all. */
    fun testNoMinimumVersionProducesNoWarning() {
        withMinVersion(null) {
            val warnings = versionWarnings("#define Text Format(\"%d\", 1)\n[Setup]\nAppName={#SysPath}\n")
            assertTrue("Without a minimum version no version WARNING may be produced", warnings.isEmpty())
        }
    }

    /** An identifier that is not followed by a call parenthesis is no function usage (a user macro named like one). */
    fun testFunctionNameWithoutCallIsNotReported() {
        withMinVersion("7.0") {
            val warnings = versionWarnings("#define Format 1\n[Setup]\nAppName=Test\n")
            assertTrue("A macro named 'Format' without call must not be reported as function", warnings.isEmpty())
        }
    }

    /** `#env`, `#echo` and `#call` exist in all supported versions: no version warning, whatever the minimum is. */
    fun testLongExistingDirectivesAreNeverReported() {
        withMinVersion("6.0") {
            val warnings = versionWarnings("#define A 1\n#env HOME\n#echo A\n#call A\n[Setup]\nAppName=Test\n")
            assertTrue("'#env', '#echo' and '#call' must not carry a version restriction", warnings.isEmpty())
        }
    }

    /** The shorthands `#?`, `#^` and `#.` are known directives: no "Unknown preprocessor directive" error. */
    fun testShorthandDirectivesAreKnown() {
        val errors = highlights("#? 1\n#^\n#.\n[Setup]\nAppName=Test\n")
            .filter { it.severity == HighlightSeverity.ERROR && it.description?.contains("Unknown preprocessor directive") == true }
        assertTrue("The shorthands #? #^ #. must be known directives, but got: ${errors.map { it.description }}", errors.isEmpty())
    }

    /** A shorthand `#^` without a preceding `#?`/`#if` is reported as a stray branch like `#else`. */
    fun testStrayShorthandElseIsReported() {
        val problems = highlights("#^\n[Setup]\nAppName=Test\n")
            .filter { it.severity == HighlightSeverity.ERROR && it.description?.contains("without matching #if") == true }
        assertTrue("A stray '#^' must be reported like a stray '#else'", problems.isNotEmpty())
    }

    /** The shorthand `#?` opens a conditional that must be closed: an unterminated `#?` is reported. */
    fun testUnterminatedShorthandIfIsReported() {
        val problems = highlights("#? 1\n[Setup]\nAppName=Test\n")
            .filter { it.severity == HighlightSeverity.ERROR && it.description?.contains("Unterminated") == true }
        assertTrue("An unterminated '#?' must be reported like an unterminated '#if'", problems.isNotEmpty())
    }

    /** `#?` and `#.` form a balanced conditional, so no structure error appears. */
    fun testBalancedShorthandConditionalHasNoStructureError() {
        val problems = highlights("#? 1\n#.\n[Setup]\nAppName=Test\n")
            .filter {
                it.severity == HighlightSeverity.ERROR &&
                        (it.description?.contains("Unterminated") == true || it.description?.contains("without matching") == true)
            }
        assertTrue("'#? 1' / '#.' must be balanced, but got: ${problems.map { it.description }}", problems.isEmpty())
    }

    /** The Inno Setup 7 preprocessor entries carry the first version in which they really exist. */
    fun testSpecCarriesFirstVersionOfNewEntriesOnly() {
        val spec = service<IsPreprocessorService>().spec
        fun functionSince(name: String) = spec.builtinFunctions.first { it.name == name }.since

        assertEquals("7.1", functionSince("Format"))
        assertEquals("7.0", functionSince("GetFileCompanyString"))
        assertEquals("7.0.1", spec.predefinedVariables.first { it.name == "SysPath" }.since)
        // Existing since Inno Setup 5.5 (only documented in 7.1) — must not be bound to a version.
        listOf("env", "echo", "call").forEach { name ->
            assertNull("Directive '$name' exists in all supported versions", spec.directives.first { it.name == name }.since)
        }
        listOf("IsDirSet", "TypeOf2").forEach { name ->
            assertNull("Function '$name' exists in all supported versions", functionSince(name))
        }
    }

    /** Aliases and shorthands resolve to the directive they stand for. */
    fun testCanonicalDirectiveResolvesAliasesAndShorthands() {
        val service = service<IsPreprocessorService>()
        assertEquals("if", service.canonicalDirective("?"))
        assertEquals("else", service.canonicalDirective("^"))
        assertEquals("endif", service.canonicalDirective("."))
        assertEquals("env", service.canonicalDirective("%"))
        assertEquals("emit", service.canonicalDirective("echo"))
        assertEquals("expr", service.canonicalDirective("CALL"))
        assertEquals("define", service.canonicalDirective("define"))
    }

    /** The old `GetFile…` function names still work in Inno Setup 7 and therefore remain known and unrestricted. */
    fun testOldGetFileFunctionNamesRemainKnown() {
        val spec = service<IsPreprocessorService>().spec
        listOf("GetFileCompany", "GetFileDescription", "GetFileCopyright", "GetFileOriginalFilename", "GetFileProductVersion")
            .forEach { name ->
                val function = spec.builtinFunctions.firstOrNull { it.name == name }
                assertNotNull("Old function name '$name' must remain in the spec", function)
                assertNull("Old function name '$name' must not be restricted by 'until'", function!!.until)
            }
    }
}
