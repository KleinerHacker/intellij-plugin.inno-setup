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

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests that [IsPreprocessorAnnotator] validates `#dim`/`#redim` arrays and array element `#define`s: array
 * existence, index/size type, bounds, redim-needs-dim, inline-initialiser count — with negative cases that must
 * stay unflagged.
 */
class IsPreprocessorAnnotatorArrayTest : BasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun errorsContaining(text: String, needle: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains(needle) == true }

    private fun anyErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }

    // ── valid declarations ─────────────────────────────────────────────────────

    fun testValidArrayScriptHasNoErrors() {
        val errors = anyErrors(
            "#dim Servers[3]\n#define Servers[0] \"a\"\n#define Servers[1] \"b\"\n#redim Servers[5]\n$setupTail"
        )
        assertTrue("A valid array script must not produce errors, was: $errors", errors.isEmpty())
    }

    fun testInlineInitializerValid() {
        val errors = anyErrors("#dim A[3] {1, 2, 3}\n$setupTail")
        assertTrue("A matching inline initialiser must not error, was: $errors", errors.isEmpty())
    }

    // ── element assignment ──────────────────────────────────────────────────────

    fun testElementDefineOnNonArrayProducesError() {
        val errors = errorsContaining("#define Bad[0] 1\n$setupTail", "is not declared as an array")
        assertTrue("Assigning an element of an undeclared array must error", errors.isNotEmpty())
    }

    fun testStaticOutOfBoundsAssignmentProducesError() {
        val errors = errorsContaining("#dim A[3]\n#define A[9] 1\n$setupTail", "out of bounds")
        assertTrue("A static out-of-bounds element assignment must error", errors.isNotEmpty())
    }

    fun testInBoundsAssignmentNoError() {
        val errors = errorsContaining("#dim A[3]\n#define A[2] 1\n$setupTail", "out of bounds")
        assertTrue("An in-bounds assignment must not error, was: $errors", errors.isEmpty())
    }

    // ── reads in expressions ────────────────────────────────────────────────────

    fun testIndexingNonArrayProducesError() {
        val errors = errorsContaining("#define Foo 1\n#define X Foo[0]\n$setupTail", "not an array")
        assertTrue("Indexing a scalar #define must error", errors.isNotEmpty())
    }

    fun testArrayWithoutIndexProducesError() {
        val errors = errorsContaining("#dim A[2]\n#define X A\n$setupTail", "must be indexed")
        assertTrue("Using an array without an index must error", errors.isNotEmpty())
    }

    fun testNonIntegerIndexProducesError() {
        val errors = errorsContaining("#dim A[2]\n#define X A[\"k\"]\n$setupTail", "must be an integer")
        assertTrue("A string index must error", errors.isNotEmpty())
    }

    fun testStaticOutOfBoundsReadProducesError() {
        val errors = errorsContaining("#dim A[3]\n#define X A[9]\n$setupTail", "out of bounds")
        assertTrue("A static out-of-bounds read must error", errors.isNotEmpty())
    }

    fun testValidReadNoError() {
        val errors = anyErrors("#dim A[3]\n#define A[0] 5\n#define X A[0]\n$setupTail")
        assertTrue("A valid array read must not error, was: $errors", errors.isEmpty())
    }

    fun testDimOfNoUnresolvedError() {
        val errors = errorsContaining("#dim A[3]\n#define X DimOf(A)\n$setupTail", "Unresolved")
        assertTrue("DimOf(array) must not report an unresolved reference, was: $errors", errors.isEmpty())
    }

    // ── #redim ──────────────────────────────────────────────────────────────────

    fun testRedimWithoutDimProducesError() {
        val errors = errorsContaining("#redim A[3]\n$setupTail", "no matching #dim")
        assertTrue("A #redim without a #dim must error", errors.isNotEmpty())
    }

    fun testRedimWithDimNoError() {
        val errors = errorsContaining("#dim A[3]\n#redim A[5]\n$setupTail", "no matching #dim")
        assertTrue("A #redim with a preceding #dim must not error, was: $errors", errors.isEmpty())
    }

    fun testOutOfBoundsAfterRedimShrink() {
        val errors = errorsContaining("#dim A[10]\n#redim A[3]\n#define A[5] 1\n$setupTail", "out of bounds")
        assertTrue("An index valid before but out of bounds after #redim shrink must error", errors.isNotEmpty())
    }

    // ── size / inline initialiser ───────────────────────────────────────────────

    fun testNonIntegerSizeProducesError() {
        val errors = errorsContaining("#dim A[\"x\"]\n$setupTail", "Array size must be an integer")
        assertTrue("A non-integer size must error", errors.isNotEmpty())
    }

    fun testNonPositiveSizeProducesError() {
        val errors = errorsContaining("#dim A[0]\n$setupTail", "Array size must be positive")
        assertTrue("A zero/negative size must error", errors.isNotEmpty())
    }

    fun testUndefOfDimArrayProducesError() {
        val errors = errorsContaining("#dim A[3]\n#undef A\n$setupTail", "#undef cannot be applied")
        assertTrue("A #undef of a #dim array must error", errors.isNotEmpty())
    }

    fun testEmptyElementIndexProducesError() {
        val errors = errorsContaining("#dim A[3]\n#define A[] 1\n$setupTail", "index is required")
        assertTrue("An empty element index '[]' must error", errors.isNotEmpty())
    }

    fun testElementAssignmentWithoutValueProducesError() {
        val errors = errorsContaining("#dim A[3]\n#define A[0]\n$setupTail", "requires a value")
        assertTrue("An array element assignment without a value must error", errors.isNotEmpty())
    }

    fun testEmptySizeProducesError() {
        val errors = errorsContaining("#dim A[]\n$setupTail", "requires a size")
        assertTrue("An empty '[]' size must error", errors.isNotEmpty())
    }

    fun testBlankSizeProducesError() {
        val errors = errorsContaining("#dim A[  ]\n$setupTail", "requires a size")
        assertTrue("A blank '[ ]' size must error", errors.isNotEmpty())
    }

    fun testInitializerCountMismatchProducesError() {
        val errors = errorsContaining("#dim A[3] {1, 2}\n$setupTail", "initializer")
        assertTrue("An initialiser count not matching the size must error", errors.isNotEmpty())
    }
}
