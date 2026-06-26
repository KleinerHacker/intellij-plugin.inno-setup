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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.lang.annotation.HighlightSeverity
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

class IsSectionAnnotatorPreprocessorTest : IsTimedBasePlatformTestCase() {

    fun testKnownIsppConstantProducesNoError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue(
            "Known ISPP constant {#AppVersion} should produce no 'Unknown constant' error",
            errors.isEmpty()
        )
    }

    fun testUnknownIsppConstantProducesError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue("Unknown ISPP constant {#Unknown} should produce an error", errors.isNotEmpty())
    }

    // ── Predefined ISPP variables via {#…} ─────────────────────────────────────

    private fun unknownConstantErrors(text: String) =
        myFixture.let {
            it.configureByText(IsScriptFileType.INSTANCE, text)
            it.doHighlighting()
        }.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }

    fun testValueBearingPredefinedVariableProducesNoError() {
        // {#SourcePath} is a value-bearing predefined variable and a valid inline emission.
        val errors = unknownConstantErrors(
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#SourcePath}\"\n"
        )
        assertTrue("{#SourcePath} must not be flagged as unknown constant", errors.isEmpty())
    }

    fun testValueBearingPredefinedVariableIsCaseInsensitive() {
        val errors = unknownConstantErrors(
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#ver}\"\n"
        )
        assertTrue("{#ver} (predefined Ver) must not be flagged, case-insensitively", errors.isEmpty())
    }

    fun testVoidPredefinedSymbolIsNotAValidEmission() {
        // WINDOWS is a valueless `void` symbol — only defined for #ifdef, not emittable via {#…}.
        val errors = unknownConstantErrors(
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#WINDOWS}\"\n"
        )
        assertTrue("{#WINDOWS} (void symbol) must still be flagged — not a valid {#…} emission", errors.isNotEmpty())
    }

    // ── Unused #define ────────────────────────────────────────────────────────

    fun testUnusedDefineProducesWeakWarning() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"value\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertTrue("Unused #define must produce a 'never used' WEAK_WARNING", hit)
    }

    fun testUnusedDefineIsHighlightedAsUnused() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"value\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.severity == HighlightSeverity.WEAK_WARNING
        }
        assertTrue("Unused #define must use the UNUSED text attribute", hit)
    }

    fun testUsedDefineViaConstantReferenceProducesNoUnusedWarning() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVer \"1.0\"\n[Setup]\nAppName=Test\nAppVersion={#AppVer}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertFalse("#define used via {#Name} must not produce a 'never used' warning", hit)
    }

    fun testUnusedDefineHighlightCoversOnlyTheName() {
        val text = "#define UnusedConst \"value\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        val highlights = myFixture.doHighlighting()
        val unused = highlights.first {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.severity == HighlightSeverity.WEAK_WARNING
        }
        // Only the name identifier is grayed — not the whole line — so the value keeps its highlighting.
        assertEquals("UnusedConst", text.substring(unused.startOffset, unused.endOffset))
    }

    // ── String with non-+ operator ────────────────────────────────────────────

    fun testStringTimesMacroParameterIsError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) \"abc\" * x\n[Setup]\nAppName={#func(1)}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("cannot be applied to a string operand", ignoreCase = true) == true
        }
        assertTrue("'\"abc\" * x' must be an error even though x is a macro parameter", hit)
    }

    // ── Function-like macro referenced without arguments ──────────────────────

    fun testFunctionMacroReferencedWithoutArgumentsIsError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) x\n#define myVar func\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("must be called with an argument list", ignoreCase = true) == true
        }
        assertTrue("A function-like macro used without an argument list must be an error", hit)
    }

    fun testFunctionMacroCalledWithArgumentsIsNoError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) x\n#define myVar func(1)\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    (it.description?.contains("must be called with an argument list", ignoreCase = true) == true ||
                            it.description?.contains("expects", ignoreCase = true) == true)
        }
        assertFalse("A correctly called function-like macro must not be flagged", hit)
    }

    fun testFunctionMacroCalledWithWrongArgumentCountIsError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) x\n#define myVar func(1, 2)\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("expects 1 argument", ignoreCase = true) == true
        }
        assertTrue("Calling a 1-parameter macro with 2 arguments must be an error", hit)
    }

    fun testFunctionMacroCalledWithWrongArgumentTypeIsError() {
        // twice(x) x * 2 constrains x to int; calling twice("a") passes a string.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define twice(x) x * 2\n#define myVar twice(\"a\")\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("must be int", ignoreCase = true) == true
        }
        assertTrue("Passing a string to an int parameter must be a type error", hit)
    }

    fun testFunctionMacroCalledWithCorrectArgumentTypeIsNoError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define twice(x) x * 2\n#define myVar twice(21)\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("of macro", ignoreCase = true) == true
        }
        assertFalse("A correctly typed argument must not be flagged", hit)
    }

    fun testFunctionMacroReturnTypeFlowsIntoCaller() {
        // func("x") returns str ("abc" + str); str + intVar (int) must be flagged.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) \"abc\" + x\n#define intVar 10\n#define myVar func(\"x\") + intVar\n" +
                "[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("cannot combine a string operand with an integer operand", ignoreCase = true) == true
        }
        assertTrue("Inferred str return type of func must make 'func(\"x\") + intVar' an error", hit)
    }

    fun testFunctionMacroReturnTypeProducesNoFalsePositive() {
        // func("x") returns str; str + "y" (str) is valid — no error.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define func(x) \"abc\" + x\n#define myVar func(\"x\") + \"y\"\n[Setup]\nAppName={#myVar}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("string operand", ignoreCase = true) == true
        }
        assertFalse("A valid str + str via an inferred macro return type must not be flagged", hit)
    }

    fun testUsedDefineViaCrossReferenceProducesNoUnusedWarning() {
        // Base is used by Full's expression; Full is used via {#Full}.
        // Neither must be flagged as unused.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Base \"1\"\n#define Full Base\n[Setup]\nAppName={#Full}\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val baseWarnings = highlights.filter {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("'Base'", ignoreCase = true) == true &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertTrue(
            "'Base' used in another #define expression must not be flagged as unused",
            baseWarnings.isEmpty()
        )
    }
}
