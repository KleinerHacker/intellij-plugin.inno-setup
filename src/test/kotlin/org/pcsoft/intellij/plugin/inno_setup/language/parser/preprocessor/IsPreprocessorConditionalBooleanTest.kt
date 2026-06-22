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
 * Tests that a literal boolean (`true`/`false`/`yes`/`no`) directly used in a `#if`/`#elif` condition is
 * flagged with the weak "boolean literal" warning — and never additionally as a red unresolved error.
 */
class IsPreprocessorConditionalBooleanTest : BasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun booleanWarnings(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "WEAK WARNING" && it.description?.contains("no boolean literals") == true }

    private fun allErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }

    fun testYesProducesBooleanWarning() {
        val warnings = booleanWarnings("#if yes\n#endif\n$setupTail")
        assertTrue("'yes' in #if must produce the boolean warning", warnings.isNotEmpty())
    }

    fun testTrueAndFalseBothFlagged() {
        val warnings = booleanWarnings("#if true || false\n#endif\n$setupTail")
        assertEquals("Both 'true' and 'false' must be flagged", 2, warnings.size)
    }

    fun testBooleanLiteralIsNotAlsoUnresolvedError() {
        val errors = allErrors("#if yes\n#endif\n$setupTail")
            .filter { it.description?.contains("Unresolved preprocessor reference") == true }
        assertTrue("A boolean literal must not also be a red unresolved error", errors.isEmpty())
    }

    fun testElifBooleanIsFlagged() {
        val warnings = booleanWarnings("#if 1\n#elif no\n#endif\n$setupTail")
        assertTrue("'no' in #elif must produce the boolean warning", warnings.isNotEmpty())
    }

    fun testIntegerConditionHasNoBooleanWarning() {
        assertTrue("An integer condition must not warn", booleanWarnings("#if 1\n#endif\n$setupTail").isEmpty())
    }

    fun testDefineConditionHasNoBooleanWarning() {
        assertTrue(
            "A define reference must not warn as boolean",
            booleanWarnings("#define Foo 1\n#if Foo\n#endif\n$setupTail").isEmpty()
        )
    }
}
