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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.preprocessor

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/** Tests the `#sub`/`#endsub` block-structure validation in [org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorAnnotator]. */
class IsPreprocessorSubroutineStructureAnnotatorTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun structureErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
            .map { it.description.orEmpty() }
            .filter { it.contains("#sub") || it.contains("#endsub") }

    fun testSubEndsubIsValid() {
        assertTrue(structureErrors("#sub Foo\n#define X 1\n#endsub\n$setupTail").isEmpty())
    }

    fun testNestedSubEndsubIsValid() {
        assertTrue(structureErrors("#sub Outer\n#sub Inner\n#endsub\n#endsub\n$setupTail").isEmpty())
    }

    fun testUnterminatedSubProducesError() {
        val errors = structureErrors("#sub Foo\n#define X 1\n$setupTail")
        assertTrue("A #sub without #endsub must be flagged: $errors", errors.any { it.contains("missing #endsub") })
    }

    fun testStrayEndsubProducesError() {
        val errors = structureErrors("#endsub\n$setupTail")
        assertTrue("A stray #endsub must be flagged: $errors", errors.any { it.contains("without matching #sub") })
    }

    fun testSubWithoutNameProducesError() {
        val errors = structureErrors("#sub\n#endsub\n$setupTail")
        assertTrue("A #sub without a name must be flagged: $errors", errors.any { it.contains("#sub requires a name") })
    }
}
