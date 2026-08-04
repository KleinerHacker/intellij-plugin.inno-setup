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

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Integration test for the `\` line continuations of preprocessor directives (Rules 9–10), applied to a
 * complete Inno Setup script instead of an isolated directive: the formatter must normalize the whitespace
 * around every `\`, indent every continued line and still apply all other rules to the surrounding sections,
 * without ever pulling a continued line back onto its predecessor.
 */
class IsSectionFormatterContinuationIT : IsTimedBasePlatformTestCase() {

    /**
     * Reformats a full script whose `[Setup]` header mixes continued `#define` directives (expression,
     * function macro, `#if` condition) with ordinary keys, followed by parameter sections and a protected
     * `[Code]` section. Verifies in one pass that
     * - the whitespace before each `\` collapses to a single space,
     * - each continued line receives the continuation indent of eight spaces,
     * - the operator, bracket and `=` rules run on both physical lines of a continued directive,
     * - the surrounding sections are formatted by their own rules and `[Code]` stays untouched.
     */
    fun testCompleteScriptWithContinuationsIsFormatted() =
        assertReformat(
            """
            [Setup]
            #define AppName "Demo"
            #define Major 1
            #define Minor 2
            #define Version Str(Major) + "." +   \
            Str(Minor)
            #define Sum( a,b ) a+ \
                                  b
            #if Major>0 && \
              Minor>1
            AppId={#AppName}
              AppVersion   =   {#Version}
            #endif
            [Files]
            Source: "app.exe" ;DestDir:"{app}"
            [Code]
                x:=Sum(a+b);
            """.trimIndent() + "\n",
            """
            [Setup]
            #define AppName "Demo"
            #define Major 1
            #define Minor 2
            #define Version Str(Major) + "." + \
                    Str(Minor)
            #define Sum(a,b) a + \
                    b
            #if Major>0 && \
                    Minor>1
            AppId = {#AppName}
            AppVersion = {#Version}
            #endif

            [Files]
            Source: "app.exe"; DestDir: "{app}"

            [Code]
                x:=Sum(a+b);
            """.trimIndent() + "\n",
        )

    /**
     * Reformatting the already formatted result of [testCompleteScriptWithContinuationsIsFormatted] must be a
     * no-op: the continuation rules are idempotent over a complete script.
     */
    fun testFormattedScriptWithContinuationsIsStable() {
        val formatted = """
            [Setup]
            #define Version Str(Major) + "." + \
                    Str(Minor)
            #define Sum(a,b) a + \
                    b

            [Files]
            Source: "app.exe"; DestDir: "{app}"
            """.trimIndent() + "\n"
        assertReformat(formatted, formatted)
    }

    /**
     * A complete script whose continuations all carry pointless trailing spaces and tabs behind the `\`: such
     * whitespace is tolerated by the lexer — the directive still continues on the next physical line — and is
     * stripped by the formatter, so the result is the very same script as without it.
     */
    fun testTrailingWhitespaceBehindContinuationIsStripped() =
        assertReformat(
            "[Setup]\n" +
                    "#define Version Str(Major) + \".\" + \\  \n" +
                    "Str(Minor)\n" +
                    "#define Sum(a,b) a+ \\\t \n" +
                    "        b\n" +
                    "AppVersion={#Version}\n",
            "[Setup]\n" +
                    "#define Version Str(Major) + \".\" + \\\n" +
                    "        Str(Minor)\n" +
                    "#define Sum(a,b) a + \\\n" +
                    "        b\n" +
                    "AppVersion = {#Version}\n",
        )

    private fun assertReformat(input: String, expected: String) {
        myFixture.configureByText("test.iss", input)
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(myFixture.file)
        }
        myFixture.checkResult(expected)
    }
}
