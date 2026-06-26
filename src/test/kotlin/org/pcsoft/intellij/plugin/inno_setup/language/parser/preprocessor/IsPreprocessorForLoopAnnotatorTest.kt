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

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests the `#for {Init; Cond; Incr} Body` validation in [IsPreprocessorAnnotator]: simple counting loops,
 * the official FindFirst `#sub`+`#for` combination, and the various error cases (missing loop variable,
 * non-integer condition, ill-typed body expression).
 */
class IsPreprocessorForLoopAnnotatorTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun errors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
            .map { it.description.orEmpty() }

    // ── valid loops ─────────────────────────────────────────────────────────────

    fun testSimpleCountingLoopIsValid() {
        val text = "#sub Body\n#endsub\n#for {i = 0; i < 10; i++} Body\n$setupTail"
        assertTrue("A simple counting loop must be valid: ${errors(text)}", errors(text).isEmpty())
    }

    fun testDecrementingCountingLoopIsValid() {
        val text = "#sub Body\n#endsub\n#for {i = 200; i > 0; i--} Body\n$setupTail"
        assertTrue("A decrementing counting loop must be valid: ${errors(text)}", errors(text).isEmpty())
    }

    fun testFindFirstSubForCombinationIsValid() {
        val text = "#define FindHandle\n#define FindResult\n#define Mask \"*.pas\"\n" +
            "#sub ProcessFoundFile\n#define FileName FindGetFileName(FindHandle)\n#endsub\n" +
            "#for {FindHandle = FindResult = FindFirst(Mask, 0); FindResult; FindResult = FindNext(FindHandle)} ProcessFoundFile\n" +
            setupTail
        assertTrue("The official FindFirst loop must be valid: ${errors(text)}", errors(text).isEmpty())
    }

    fun testComplexBodyExpressionIsValid() {
        val text = "#define Step 2\n#sub Emit\n#endsub\n#for {i = 0; i < Step * 5; i = i + Step} Emit\n$setupTail"
        assertTrue("A complex condition/increment must be valid: ${errors(text)}", errors(text).isEmpty())
    }

    // ── error cases ───────────────────────────────────────────────────────────────

    fun testMissingLoopVariableProducesError() {
        val text = "#sub Body\n#endsub\n#for {0; i < 10; i++} Body\n$setupTail"
        assertTrue(
            "An initializer without an assignment must be flagged: ${errors(text)}",
            errors(text).any { it.contains("must define a loop variable") },
        )
    }

    fun testNonIntegerConditionProducesError() {
        val text = "#sub Body\n#endsub\n#for {i = 0; \"x\"; i++} Body\n$setupTail"
        assertTrue(
            "A string condition must be flagged: ${errors(text)}",
            errors(text).any { it.contains("condition must be an integer") },
        )
    }

    fun testMissingBraceGroupProducesError() {
        val text = "#for i = 0\n$setupTail"
        assertTrue(
            "A #for without a brace group must be flagged: ${errors(text)}",
            errors(text).any { it.contains("#for requires") },
        )
    }

    fun testIllTypedBodyExpressionProducesError() {
        // The body `"a" * 1` mixes a string operand with `*` — an expression type error.
        val text = "#for {i = 0; i < 1; i++} \"a\" * 1\n$setupTail"
        assertTrue(
            "An ill-typed body expression must be flagged: ${errors(text)}",
            errors(text).any { it.contains("string operand") },
        )
    }
}
