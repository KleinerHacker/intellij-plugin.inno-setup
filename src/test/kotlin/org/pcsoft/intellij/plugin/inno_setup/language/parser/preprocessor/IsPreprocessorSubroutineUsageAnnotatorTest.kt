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
 * Tests the `#sub` *usage* rules in [IsPreprocessorAnnotator]: a subroutine may only be called as the body of a
 * `#for` loop (nowhere else), and a `#sub` declaration name must be a plain identifier.
 */
class IsPreprocessorSubroutineUsageAnnotatorTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun errors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
            .map { it.description.orEmpty() }

    // ── #sub only valid as a #for body ─────────────────────────────────────────

    fun testSubAsForBodyIsValid() {
        val text = "#sub AddFile\n#endsub\n#for {i = 200; i > 0; i--} AddFile\n$setupTail"
        assertTrue("A #sub used as the #for body must be valid: ${errors(text)}", errors(text).isEmpty())
    }

    fun testSubUsedInDefineExpressionIsError() {
        val text = "#sub AddFile\n#endsub\n#define X AddFile\n$setupTail"
        assertTrue(
            "Calling a #sub in a #define expression must be flagged: ${errors(text)}",
            errors(text).any { it.contains("#sub and can only be called as the body of a #for") },
        )
    }

    fun testSubUsedInIfConditionIsError() {
        val text = "#sub AddFile\n#endsub\n#if AddFile\n#endif\n$setupTail"
        assertTrue(
            "Calling a #sub in a #if condition must be flagged: ${errors(text)}",
            errors(text).any { it.contains("#sub and can only be called as the body of a #for") },
        )
    }

    fun testSubUsedInForConditionIsError() {
        // Using the sub name in the condition (not the body) is a misuse.
        val text = "#sub AddFile\n#endsub\n#for {i = 0; AddFile; i++} AddFile\n$setupTail"
        assertTrue(
            "Calling a #sub in the #for condition must be flagged: ${errors(text)}",
            errors(text).any { it.contains("#sub and can only be called as the body of a #for") },
        )
    }

    // ── invalid #sub names ──────────────────────────────────────────────────────

    fun testHyphenInSubNameIsError() {
        val text = "#sub My-Name\n#endsub\n$setupTail"
        assertTrue(
            "A hyphen in a #sub name must be flagged: ${errors(text)}",
            errors(text).any { it.contains("Invalid #sub name") },
        )
    }

    fun testDigitLeadingSubNameIsError() {
        val text = "#sub 1Name\n#endsub\n$setupTail"
        assertTrue(
            "A digit-leading #sub name must be flagged: ${errors(text)}",
            errors(text).any { it.contains("Invalid #sub name") },
        )
    }

    fun testTrailingTokensInSubNameIsError() {
        val text = "#sub Name extra\n#endsub\n$setupTail"
        assertTrue(
            "Trailing tokens after a #sub name must be flagged: ${errors(text)}",
            errors(text).any { it.contains("Invalid #sub name") },
        )
    }

    fun testPlainSubNameIsValid() {
        val text = "#sub Valid_Name1\n#endsub\n$setupTail"
        assertTrue(
            "A plain identifier #sub name must be valid: ${errors(text)}",
            errors(text).none { it.contains("Invalid #sub name") },
        )
    }
}
