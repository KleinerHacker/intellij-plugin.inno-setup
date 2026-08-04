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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * End-to-end reformat tests for the Inno Setup code formatter (all rules of the default configuration), driven
 * through [CodeStyleManager.reformat] so both the model pass and [IsFormatterPostFormatProcessor] run.
 */
class IsSectionFormatterTest : IsTimedBasePlatformTestCase() {

    // ── Rule 2: header brackets ───────────────────────────────────────────────

    fun testHeaderBracketsAreTightened() =
        assertReformat("[ Setup ]\nAppName = A\n", "[Setup]\nAppName = A\n")

    // ── Rule 3.1: space around '=' ────────────────────────────────────────────

    fun testSpaceAroundAssignIsNormalized() =
        assertReformat("[Setup]\nAppName=A\nAppVersion   =   1.0\n", "[Setup]\nAppName = A\nAppVersion = 1.0\n")

    // ── Rule 3.2 / 4.2: leading indentation ───────────────────────────────────

    fun testLeadingIndentationIsRemoved() =
        assertReformat("[Setup]\n    AppName = A\n", "[Setup]\nAppName = A\n")

    fun testLeadingIndentationIsRemovedForEveryKeyNotJustTheFirst() =
        assertReformat(
            "[Setup]\n    AppName = A\n    AppVersion = B\n    AppId = C\n",
            "[Setup]\nAppName = A\nAppVersion = B\nAppId = C\n",
        )

    // ── Rule 4: parameter pairs ───────────────────────────────────────────────

    fun testParameterPairSpacingIsNormalized() =
        assertReformat(
            "[Files]\nSource: \"a\" ;DestDir:\"b\"\n",
            "[Files]\nSource: \"a\"; DestDir: \"b\"\n",
        )

    // ── Rule 1: blank line between sections ───────────────────────────────────

    fun testTooManyBlankLinesAreCollapsed() =
        assertReformat(
            "[Setup]\nAppName = A\n\n\n\n[Files]\nSource: \"a\"\n",
            "[Setup]\nAppName = A\n\n[Files]\nSource: \"a\"\n",
        )

    fun testMissingBlankLineIsInserted() =
        assertReformat(
            "[Setup]\nAppName = A\n[Files]\nSource: \"a\"\n",
            "[Setup]\nAppName = A\n\n[Files]\nSource: \"a\"\n",
        )

    // ── Rule 5: preprocessor arithmetic operators ─────────────────────────────

    fun testPreprocessorArithmeticOperatorsGetSpaces() =
        assertReformat("[Setup]\n#define X 2+3*4\n", "[Setup]\n#define X 2 + 3 * 4\n")

    fun testPreprocessorBinaryMinusGetsSpaces() =
        assertReformat("[Setup]\n#define X 5-2\n", "[Setup]\n#define X 5 - 2\n")

    fun testPreprocessorUnaryMinusIsPreserved() =
        assertReformat("[Setup]\n#define X -3\n", "[Setup]\n#define X -3\n")

    fun testPreprocessorMinusInsideStringIsPreserved() =
        assertReformat("[Setup]\n#define V \"1.2-beta\"\n", "[Setup]\n#define V \"1.2-beta\"\n")

    fun testPreprocessorIncrementIsNotSpaced() =
        assertReformat("[Setup]\n#define X i++\n", "[Setup]\n#define X i++\n")

    fun testPreprocessorDecrementIsNotSpaced() =
        assertReformat("[Setup]\n#define X i--\n", "[Setup]\n#define X i--\n")

    // ── Rules 6–8: preprocessor brackets / '=' / ';' ──────────────────────────

    fun testForLoopHeaderIsFormatted() =
        assertReformat("[Setup]\n#for {i=0;i<3;i++} X\n", "[Setup]\n#for {i = 0; i<3; i++} X\n")

    fun testForLoopBracesAndSpacesAreTightened() =
        assertReformat("[Setup]\n#for { i=0; i<3; i++ } X\n", "[Setup]\n#for {i = 0; i<3; i++} X\n")

    fun testForLoopEqualityOperatorIsNotBrokenByAssignRule() =
        assertReformat("[Setup]\n#for {i=0; i==n; i++} X\n", "[Setup]\n#for {i = 0; i==n; i++} X\n")

    fun testFunctionMacroParenthesesAreTightened() =
        assertReformat("[Setup]\n#define Add( a,b ) a+b\n", "[Setup]\n#define Add(a,b) a + b\n")

    fun testArrayBracketsAreTightened() =
        assertReformat("[Setup]\n#define Arr[ 0 ] 5\n", "[Setup]\n#define Arr[0] 5\n")

    fun testInnoConstantBracesInStringAreLeftUntouched() =
        assertReformat("[Setup]\n#define P \"{app}\\x\"\n", "[Setup]\n#define P \"{app}\\x\"\n")

    fun testPreprocessorConditionOperators() =
        assertReformat("[Setup]\n#if A>1+2\n#endif\n", "[Setup]\n#if A>1 + 2\n#endif\n")

    // ── Rules 9–10: line continuations ────────────────────────────────────────

    /**
     * A directive continued with a trailing `\` keeps its line structure: the continuation survives the
     * reformat and the operator rule is applied on both physical lines.
     */
    fun testContinuedDirectiveKeepsItsContinuation() =
        assertReformat(
            "[Setup]\n#define X 2+3 \\\n        +4\n",
            "[Setup]\n#define X 2 + 3 \\\n        + 4\n",
        )

    /**
     * The whitespace before the trailing `\` is normalized to exactly one space (Rule 9).
     */
    fun testSpaceBeforeContinuationIsNormalized() =
        assertReformat(
            "[Setup]\n#define X 1   \\\n        2\n",
            "[Setup]\n#define X 1 \\\n        2\n",
        )

    /**
     * A `\` followed by nothing but whitespace still continues the directive, and that pointless trailing
     * whitespace is stripped by Rule 9 together with the usual spacing and indent normalization.
     */
    fun testWhitespaceBehindContinuationIsStripped() =
        assertReformat(
            "[Setup]\n#define X 1 \\  \n2\n",
            "[Setup]\n#define X 1 \\\n        2\n",
        )

    /**
     * A continued line is indented by the continuation indent, whatever indentation it had before (Rule 10).
     */
    fun testContinuedLineGetsContinuationIndent() =
        assertReformat(
            "[Setup]\n#define X 1 + \\\n2\n",
            "[Setup]\n#define X 1 + \\\n        2\n",
        )

    /**
     * The bracket rule must not tighten across a line break: a `(` at the end of a continued line keeps the
     * continuation instead of pulling the next line's first argument up.
     */
    fun testBracketRuleDoesNotActAcrossContinuation() =
        assertReformat(
            "[Setup]\n#define Sum(a, \\\n        b) a+b\n",
            "[Setup]\n#define Sum(a, \\\n        b) a + b\n",
        )

    /** With the indent option off, the indentation of a continued line is left exactly as written. */
    fun testDisablingContinuationIndentKeepsIndentUntouched() {
        val settings = CodeStyle.createTestSettings(CodeStyle.getSettings(project))
        settings.getCustomSettings(IsSectionCodeStyleSettings::class.java).PP_INDENT_CONTINUATION = false
        CodeStyle.doWithTemporarySettings(project, settings, Runnable {
            assertReformat("[Setup]\n#define X 1 + \\\n2\n", "[Setup]\n#define X 1 + \\\n2\n")
        })
    }

    /** With the continuation-space option off, the whitespace around the `\` is left exactly as written. */
    fun testDisablingContinuationSpacingKeepsBackslashUntouched() {
        val settings = CodeStyle.createTestSettings(CodeStyle.getSettings(project))
        val custom = settings.getCustomSettings(IsSectionCodeStyleSettings::class.java)
        custom.PP_SPACE_BEFORE_CONTINUATION = false
        custom.PP_INDENT_CONTINUATION = false
        CodeStyle.doWithTemporarySettings(project, settings, Runnable {
            assertReformat("[Setup]\n#define X 1   \\\n2\n", "[Setup]\n#define X 1   \\\n2\n")
        })
    }

    // ── Idempotency & protected [Code] ────────────────────────────────────────

    fun testAlreadyFormattedIsUnchanged() {
        val formatted = "[Setup]\nAppName = A\n\n[Files]\nSource: \"a\"; DestDir: \"b\"\n"
        assertReformat(formatted, formatted)
    }

    fun testCodeSectionIsNotReformatted() {
        val code = "[Code]\n    x := 5;\n    y:=Foo(a+b);\n"
        assertReformat(code, code)
    }

    // ── Option toggles ────────────────────────────────────────────────────────

    fun testDisablingAssignSpacingKeepsAssignUntouched() {
        val settings = CodeStyle.createTestSettings(CodeStyle.getSettings(project))
        settings.getCustomSettings(IsSectionCodeStyleSettings::class.java).SPACE_AROUND_ASSIGN = false
        CodeStyle.doWithTemporarySettings(project, settings, Runnable {
            assertReformat("[Setup]\nAppName=A\n", "[Setup]\nAppName=A\n")
        })
    }

    fun testDisablingPreprocessorOperatorSpacingKeepsExpressionUntouched() {
        val settings = CodeStyle.createTestSettings(CodeStyle.getSettings(project))
        settings.getCustomSettings(IsSectionCodeStyleSettings::class.java).SPACE_AROUND_PP_OPERATORS = false
        CodeStyle.doWithTemporarySettings(project, settings, Runnable {
            assertReformat("[Setup]\n#define X 2+3\n", "[Setup]\n#define X 2+3\n")
        })
    }

    private fun assertReformat(input: String, expected: String) {
        myFixture.configureByText("test.iss", input)
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(myFixture.file)
        }
        myFixture.checkResult(expected)
    }
}
