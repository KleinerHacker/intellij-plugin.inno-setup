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
 * Tests for `#if`/`#elif` condition-expression analysis in [IsPreprocessorAnnotator]: type errors,
 * reference resolution (unresolved identifiers are errors, like in `#define`) and the `defined(...)` escape.
 */
class IsPreprocessorConditionalExpressionTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun errorsContaining(text: String, needle: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains(needle) == true }

    fun testTypeErrorInIfConditionIsMarked() {
        // Multiplying two strings is a type violation, reported by the shared expression engine.
        val errors = myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, "#if \"a\" * \"b\"\n#endif\n$setupTail") }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
        assertTrue("A type error in an #if condition must be marked, was: ${errors.map { it.description }}",
            errors.any { (it.description ?: "").isNotEmpty() })
    }

    fun testUnresolvedReferenceInIfIsError() {
        val errors = errorsContaining("#if Foo\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("An unknown identifier in #if must be an error", errors.isNotEmpty())
        assertTrue("Error should name the identifier", errors.any { it.description?.contains("Foo") == true })
    }

    fun testResolvedReferenceInIfIsNoError() {
        val errors = errorsContaining("#define Foo 1\n#if Foo\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to an existing #define in #if must not error", errors.isEmpty())
    }

    fun testElifReferenceResolves() {
        val errors = errorsContaining("#define Foo 1\n#if 0\n#elif Foo\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A reference to an existing #define in #elif must not error", errors.isEmpty())
    }

    fun testDefinedArgumentIsNotUnresolved() {
        // defined(X) legitimately tests a possibly-undefined name → must not be an unresolved-reference error.
        val errors = errorsContaining("#if defined(Maybe)\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("defined(Maybe) must not flag 'Maybe' as unresolved, was: ${errors.map { it.description }}", errors.isEmpty())
    }

    fun testPredefinedVariableInIfIsNoError() {
        val errors = errorsContaining("#if PREPROCVER\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("A predefined variable in #if must not error", errors.isEmpty())
    }

    // ── missing condition ─────────────────────────────────────────────────────

    fun testIfWithoutConditionIsError() {
        val errors = errorsContaining("#if\n#endif\n$setupTail", "requires a condition")
        assertTrue("#if without a condition must be an error", errors.isNotEmpty())
    }

    fun testIfWithOnlyWhitespaceConditionIsError() {
        val errors = errorsContaining("#if   \n#endif\n$setupTail", "requires a condition")
        assertTrue("#if with a blank condition must be an error", errors.isNotEmpty())
    }

    fun testElifWithoutConditionIsError() {
        val errors = errorsContaining("#if 1\n#elif\n#endif\n$setupTail", "requires a condition")
        assertTrue("#elif without a condition must be an error", errors.isNotEmpty())
    }

    fun testIfWithConditionHasNoMissingConditionError() {
        val errors = errorsContaining("#if 1\n#endif\n$setupTail", "requires a condition")
        assertTrue("#if with a condition must not raise the missing-condition error", errors.isEmpty())
    }
}
