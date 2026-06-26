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
 * Tests that [IsPreprocessorAnnotator] validates the `#pragma` directive: the sub-command must be known and
 * its argument must match the declared kind (flags / string expression / integer expression).
 */
class IsPreprocessorPragmaAnnotatorTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun errors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text + setupTail) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }

    private fun errorsContaining(text: String, needle: String) =
        errors(text).filter { it.description?.contains(needle, ignoreCase = true) == true }

    // ── valid sub-commands ────────────────────────────────────────────────────

    fun testValidSubCommandsProduceNoError() {
        val lines = listOf(
            "#pragma option -v+",
            "#pragma option -c- -e+",
            "#pragma parseroption -b- -u+",
            "#pragma message \"hello\"",
            "#pragma warning \"careful\"",
            "#pragma error \"boom\"",
            "#pragma verboselevel 9",
            "#pragma inlinestart \"\$(\"",
            "#pragma inlineend \")\"",
            "#pragma include \"D:\\inc\"",
            "#pragma spansymbol \"_\"",
        )
        lines.forEach { line ->
            assertTrue("Valid pragma must not be flagged: $line", errors(line + "\n").isEmpty())
        }
    }

    fun testSubCommandIsCaseInsensitive() {
        assertTrue("'#PRAGMA OPTION' must be accepted", errors("#PRAGMA OPTION -v+\n").isEmpty())
    }

    // ── unknown / missing sub-command ─────────────────────────────────────────

    fun testUnknownSubCommandProducesError() {
        val e = errorsContaining("#pragma frobnicate 1\n", "Unknown #pragma sub-command")
        assertTrue("An unknown sub-command must be flagged", e.isNotEmpty())
        assertTrue("Error should name the sub-command", e.any { it.description?.contains("frobnicate") == true })
    }

    fun testMissingSubCommandProducesError() {
        assertTrue("A bare #pragma must be flagged", errorsContaining("#pragma\n", "requires a sub-command").isNotEmpty())
    }

    // ── option / parseroption flags ───────────────────────────────────────────

    fun testInvalidFlagLetterProducesError() {
        assertTrue("Unknown flag letter must be flagged", errorsContaining("#pragma option -x+\n", "flag letter").isNotEmpty())
    }

    fun testMalformedFlagProducesError() {
        assertTrue("Flag without sign must be flagged", errorsContaining("#pragma option -v\n", "Invalid #pragma option flag").isNotEmpty())
        assertTrue("Flag without dash must be flagged", errorsContaining("#pragma option v+\n", "Invalid #pragma option flag").isNotEmpty())
    }

    fun testOptionLetterNotValidForParserOption() {
        // 'c' is an option flag, not a parseroption flag.
        assertTrue("An option letter must be rejected for parseroption", errorsContaining("#pragma parseroption -c+\n", "flag letter").isNotEmpty())
    }

    fun testParserOptionLetterNotValidForOption() {
        // 'u' is a parseroption flag, not an option flag.
        assertTrue("A parseroption letter must be rejected for option", errorsContaining("#pragma option -u+\n", "flag letter").isNotEmpty())
    }

    fun testFlagsMissingArgumentProducesError() {
        assertTrue("option without flags must be flagged", errorsContaining("#pragma option\n", "requires an argument").isNotEmpty())
    }

    // ── verboselevel (integer) ────────────────────────────────────────────────

    fun testVerboseLevelNonIntegerProducesError() {
        assertTrue("A non-integer verboselevel must be flagged", errorsContaining("#pragma verboselevel \"x\"\n", "requires an integer value").isNotEmpty())
    }

    fun testVerboseLevelOutOfRangeProducesError() {
        assertTrue("verboselevel out of range must be flagged", errorsContaining("#pragma verboselevel 11\n", "between 0 and 10").isNotEmpty())
    }

    fun testVerboseLevelMissingArgumentProducesError() {
        assertTrue("verboselevel without argument must be flagged", errorsContaining("#pragma verboselevel\n", "requires an argument").isNotEmpty())
    }

    // ── string-expression sub-commands ────────────────────────────────────────

    fun testStringSubCommandWithIntegerProducesError() {
        assertTrue("A non-string message must be flagged", errorsContaining("#pragma message 5\n", "requires a string value").isNotEmpty())
    }

    fun testStringSubCommandMissingArgumentProducesError() {
        assertTrue("message without argument must be flagged", errorsContaining("#pragma message\n", "requires an argument").isNotEmpty())
    }

    fun testStringConcatenationIsValid() {
        assertTrue("String concatenation must be valid", errors("#pragma message \"a\" + \"b\"\n").isEmpty())
    }

    // ── references inside expression arguments ────────────────────────────────

    fun testResolvedReferenceInPragmaExpressionNoError() {
        assertTrue(
            "A reference to an existing #define must not be flagged",
            errorsContaining("#define Msg \"hi\"\n#pragma message Msg + \"!\"\n", "Unresolved preprocessor reference").isEmpty()
        )
    }

    fun testUnresolvedReferenceInPragmaExpressionProducesError() {
        assertTrue(
            "A reference to a non-existent #define must be flagged",
            errorsContaining("#pragma message Unknown + \"!\"\n", "Unresolved preprocessor reference").isNotEmpty()
        )
    }

    fun testResolvedIntReferenceInVerboseLevelNoError() {
        assertTrue(
            "An int #define used in verboselevel must not be flagged",
            errorsContaining("#define Lvl 5\n#pragma verboselevel Lvl\n", "Unresolved preprocessor reference").isEmpty()
        )
    }
}
