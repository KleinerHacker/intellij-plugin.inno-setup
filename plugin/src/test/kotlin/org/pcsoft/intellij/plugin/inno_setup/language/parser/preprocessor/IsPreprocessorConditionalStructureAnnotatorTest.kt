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

import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for the `#if`/`#elif`/`#else`/`#endif` block-structure validation in [IsPreprocessorAnnotator].
 * `#elif`/`#else` are optional, so the matched/valid cases are tested with and without them.
 */
class IsPreprocessorConditionalStructureAnnotatorTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun structureErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
            .filter {
                val d = it.description ?: ""
                d.contains("#endif") || d.contains("without matching #if") || d.contains("after #else")
            }
            .map { it.description.orEmpty() }

    // ── valid blocks ──────────────────────────────────────────────────────────

    fun testIfEndifIsValid() {
        assertTrue(structureErrors("#if 1\n#define X 1\n#endif\n$setupTail").isEmpty())
    }

    fun testIfElifEndifIsValid() {
        assertTrue(structureErrors("#if 1\n#elif 2\n#endif\n$setupTail").isEmpty())
    }

    fun testIfElseEndifIsValid() {
        assertTrue(structureErrors("#if 1\n#else\n#endif\n$setupTail").isEmpty())
    }

    fun testIfElifElseEndifIsValid() {
        assertTrue(structureErrors("#if 1\n#elif 2\n#else\n#endif\n$setupTail").isEmpty())
    }

    fun testNestedIfEndifIsValid() {
        assertTrue(structureErrors("#if 1\n#if 2\n#endif\n#endif\n$setupTail").isEmpty())
    }

    fun testIfdefEndifIsValid() {
        assertTrue(structureErrors("#ifdef FOO\n#endif\n$setupTail").isEmpty())
    }

    fun testIfndefEndifIsValid() {
        assertTrue(structureErrors("#ifndef FOO\n#endif\n$setupTail").isEmpty())
    }

    fun testIfExistEndifIsValid() {
        assertTrue(structureErrors("#ifexist \"f.iss\"\n#endif\n$setupTail").isEmpty())
    }

    fun testIfNexistEndifIsValid() {
        assertTrue(structureErrors("#ifnexist \"f.iss\"\n#endif\n$setupTail").isEmpty())
    }

    // ── invalid blocks ────────────────────────────────────────────────────────

    fun testUnterminatedIfProducesError() {
        val errors = structureErrors("#if 1\n#define X 1\n$setupTail")
        assertTrue("An #if without #endif must be flagged: $errors", errors.any { it.contains("missing #endif") })
    }

    fun testUnterminatedIfdefProducesError() {
        val errors = structureErrors("#ifdef FOO\n$setupTail")
        assertTrue("An #ifdef without #endif must be flagged: $errors", errors.any { it.contains("missing #endif") })
    }

    fun testUnterminatedIfExistProducesError() {
        val errors = structureErrors("#ifexist \"f.iss\"\n$setupTail")
        assertTrue("An #ifexist without #endif must be flagged: $errors", errors.any { it.contains("missing #endif") })
    }

    fun testStrayEndifProducesError() {
        val errors = structureErrors("#endif\n$setupTail")
        assertTrue("A stray #endif must be flagged: $errors", errors.any { it.contains("without matching #if") })
    }

    fun testStrayElifProducesError() {
        val errors = structureErrors("#elif 1\n$setupTail")
        assertTrue("A stray #elif must be flagged: $errors", errors.any { it.contains("without matching #if") })
    }

    fun testStrayElseProducesError() {
        val errors = structureErrors("#else\n$setupTail")
        assertTrue("A stray #else must be flagged: $errors", errors.any { it.contains("without matching #if") })
    }

    fun testElifAfterElseProducesError() {
        val errors = structureErrors("#if 1\n#else\n#elif 2\n#endif\n$setupTail")
        assertTrue("#elif after #else must be flagged: $errors", errors.any { it.contains("after #else") })
    }

    fun testNestedUnterminatedInnerIfProducesError() {
        val errors = structureErrors("#if 1\n#if 2\n#endif\n$setupTail")
        assertTrue("The outer #if without #endif must be flagged: $errors", errors.any { it.contains("missing #endif") })
    }
}
