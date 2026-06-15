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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests that [IsPreprocessorAnnotator] validates the directive keyword against the ISPP spec
 * ([org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService], the single source of truth).
 */
class IsPreprocessorAnnotatorDirectiveTest : BasePlatformTestCase() {

    private fun unknownDirectiveErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter {
                it.severity.name == "ERROR" &&
                        it.description?.contains("Unknown preprocessor directive") == true
            }

    fun testKnownDirectiveProducesNoError() {
        val errors = unknownDirectiveErrors(
            "#define MyConst \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Known directive '#define' must not be flagged", errors.isEmpty())
    }

    fun testKnownDirectiveIsCaseInsensitive() {
        // ISPP directives are case-insensitive.
        val errors = unknownDirectiveErrors(
            "#DEFINE MyConst \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("'#DEFINE' must be accepted (directives are case-insensitive)", errors.isEmpty())
    }

    fun testUnknownDirectiveProducesError() {
        val errors = unknownDirectiveErrors(
            "#frobnicate something\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Unknown directive '#frobnicate' must produce an error", errors.isNotEmpty())
        assertTrue(
            "Error message should name the offending directive",
            errors.any { it.description?.contains("#frobnicate") == true }
        )
    }

    // ── Unresolved #define expression references ──────────────────────────────

    private fun errorsContaining(text: String, needle: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains(needle) == true }

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    fun testUnresolvedExpressionReferenceProducesError() {
        val errors = errorsContaining("#define A Foo\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to a non-existent #define must produce an error", errors.isNotEmpty())
        assertTrue("Error should name the unresolved reference", errors.any { it.description?.contains("Foo") == true })
    }

    fun testResolvedExpressionReferenceNoError() {
        val errors = errorsContaining("#define Foo 1\n#define A Foo\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to an existing #define must not produce an error", errors.isEmpty())
    }

    fun testBuiltinFunctionReferenceNoError() {
        val errors = errorsContaining("#define A GetFileVersion(\"app.exe\")\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to a known ISPP built-in function must not produce an error", errors.isEmpty())
    }

    fun testPredefinedVariableReferenceNoError() {
        val errors = errorsContaining("#define A __LINE__\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to a known ISPP predefined variable must not produce an error", errors.isEmpty())
    }

    fun testMacroParametersDoNotProduceUnresolvedError() {
        // a and b are macro parameters (local) — neither the declaration nor their use in the body
        // must be reported as unresolved references.
        val errors = errorsContaining("#define Max(a, b) a > b ? a : b\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("Macro parameters must not be treated as unresolved references", errors.isEmpty())
    }

    fun testNonParameterIdentifierInMacroBodyProducesError() {
        // 'Helper' is not a parameter, not a built-in and not a #define → unresolved.
        val errors = errorsContaining("#define Max(a, b) a > Helper\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A non-parameter identifier in a macro body must be reported as unresolved", errors.isNotEmpty())
        assertTrue("Error should name the unresolved identifier", errors.any { it.description?.contains("Helper") == true })
    }

    // ── Reserved / forbidden #define names ───────────────────────────────────

    fun testForbiddenDefineNameProducesError() {
        val errors = errorsContaining("#define defined 1\n$setupTail", "reserved preprocessor keyword")
        assertTrue("A reserved keyword used as a #define name must produce an error", errors.isNotEmpty())
        assertTrue("Error should name the reserved keyword", errors.any { it.description?.contains("defined") == true })
    }

    fun testForbiddenDefineNameIsCaseInsensitive() {
        val errors = errorsContaining("#define INT 1\n$setupTail", "reserved preprocessor keyword")
        assertTrue("Reserved keyword match must be case-insensitive ('INT' == 'int')", errors.isNotEmpty())
    }

    fun testForbiddenScopeKeywordAsNameProducesError() {
        val errors = errorsContaining("#define public 1\n$setupTail", "reserved preprocessor keyword")
        assertTrue("Scope keyword 'public' must not be usable as a #define name", errors.isNotEmpty())
    }

    fun testAllowedDefineNameNoForbiddenError() {
        val errors = errorsContaining("#define MyConst 1\n$setupTail", "reserved preprocessor keyword")
        assertTrue("A non-reserved #define name must not produce a forbidden-name error", errors.isEmpty())
    }

    // ── #define name starting with a digit ───────────────────────────────────

    fun testDigitLeadingDefineNameProducesError() {
        val errors = errorsContaining("#define 1abc \"x\"\n$setupTail", "must not start with a digit")
        assertTrue("A #define name starting with a digit must produce an error", errors.isNotEmpty())
    }

    fun testValidDefineNameNoDigitError() {
        val errors = errorsContaining("#define abc \"x\"\n$setupTail", "must not start with a digit")
        assertTrue("A valid #define name must not produce a digit error", errors.isEmpty())
    }
}
