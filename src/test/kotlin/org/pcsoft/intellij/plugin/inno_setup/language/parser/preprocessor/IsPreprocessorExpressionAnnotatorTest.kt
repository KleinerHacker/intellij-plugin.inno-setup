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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * End-to-end tests of the #define expression analysis in [IsPreprocessorAnnotator]: the six user-named
 * reference scripts highlight without error, while syntax/type violations are flagged as ERROR at the
 * precise offending token.
 */
class IsPreprocessorExpressionAnnotatorTest : BasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    /** ERROR highlights whose message is an expression problem (operator/operand/type/parenthesis). */
    private fun expressionErrors(text: String): List<Pair<String, String>> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        // doHighlighting reports host-coordinate offsets; resolve the top-level (host) file so its text is
        // used for slicing even when myFixture.file points at an injected ISPP fragment.
        val hostFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)
        val docText = hostFile.text
        return myFixture.doHighlighting()
            .filter { it.severity.name == "ERROR" }
            .filter { hi ->
                val d = hi.description ?: ""
                d.contains("Operator") || d.contains("Operand") ||
                        d.contains("parenthesis") || d.contains("ternary")
            }
            .filter { it.startOffset in 0..docText.length && it.endOffset in 0..docText.length }
            .map { it.description.orEmpty() to docText.substring(it.startOffset, it.endOffset) }
    }

    private fun markedSlices(text: String) = expressionErrors(text).map { it.second }

    // ── Positive: the user reference script must be clean ─────────────────────

    fun testUserReferenceScriptHasNoExpressionErrors() {
        val script = buildString {
            append("#define Major 1\n")
            append("#define Minor 5\n")
            append("#define Version Str(Major) + \".\" + Str(Minor)\n")
            append("#define Build 100\n")
            append("#define NextBuild (Build + 1)\n")
            append("#define OutputDir \"Builds\\\\\" + Version\n")
            append(setupTail)
        }
        assertTrue("User reference script must be free of expression errors: ${markedSlices(script)}",
            markedSlices(script).isEmpty())
    }

    fun testSimpleValuesAndEmptyAndBuiltinCallAreClean() {
        assertTrue(markedSlices("#define A 5\n$setupTail").isEmpty())
        assertTrue(markedSlices("#define A \"x\"\n$setupTail").isEmpty())
        assertTrue(markedSlices("#define A\n$setupTail").isEmpty())
        assertTrue(markedSlices("#define A GetFileVersionString(\"app.exe\")\n$setupTail").isEmpty())
    }

    fun testMacroBodyWithParametersIsClean() {
        assertTrue(markedSlices("#define Max(a, b) a > b ? a : b\n$setupTail").isEmpty())
    }

    // ── Negative: exact offending token ───────────────────────────────────────

    fun testMultiplyingStringsMarksTheOperator() {
        assertEquals(listOf("*"), markedSlices("#define A \"a\" * \"b\"\n$setupTail"))
    }

    fun testAddingIntAndStringMarksThePlus() {
        assertEquals(listOf("+"), markedSlices("#define A 1 + \"s\"\n$setupTail"))
    }

    fun testUnaryMinusOnStringMarksTheOperand() {
        assertEquals(listOf("\"s\""), markedSlices("#define A -\"s\"\n$setupTail"))
    }

    fun testMissingOperatorMarksSecondOperand() {
        assertEquals(listOf("6"), markedSlices("#define A 5 6\n$setupTail"))
    }

    fun testUnbalancedParenthesisMarksTheBracket() {
        assertEquals(listOf("("), markedSlices("#define A (1 + 2\n$setupTail"))
    }

    fun testComparingStringWithIntMarksTheOperator() {
        assertEquals(listOf("<"), markedSlices("#define A \"a\" < 1\n$setupTail"))
    }

    fun testMultipleErrorsAreMarkedSeparately() {
        // "a"*"b" + 1*"c": the two string-operator conflicts each get their own marker.
        val slices = markedSlices("#define A \"a\"*\"b\" + 1*\"c\"\n$setupTail")
        assertEquals(listOf("*", "*"), slices)
    }

    // ── Recursive reference type errors ───────────────────────────────────────

    fun testRecursiveReferenceTypeConflictMarksTheOperator() {
        val script = "#define A \"x\"\n#define B 5\n#define C A * B\n$setupTail"
        // A=str, B=int → A * B is str*int → error on the '*' in line C.
        assertEquals(listOf("*"), markedSlices(script))
    }

    fun testCleanRecursiveReferencesProduceNoError() {
        val script = "#define V \"x\"\n#define W V + \"y\"\n$setupTail"
        assertTrue(markedSlices(script).isEmpty())
    }

    // ── Robustness ────────────────────────────────────────────────────────────

    fun testSelfReferenceDoesNotCrashOrFalselyError() {
        // Recursion guard: a self-reference must neither crash nor yield a recursion type error.
        val slices = markedSlices("#define P P + 1\n$setupTail")
        assertTrue("Self-reference must not produce an expression type error: $slices", slices.isEmpty())
    }
}
