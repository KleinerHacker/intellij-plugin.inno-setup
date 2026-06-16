/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression

import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprValue.IntValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprValue.StrValue

/** Verifies the static value computation of [IsPreprocessorExprEvaluator] and [IsPreprocessorExprValueResolver]. */
class IsPreprocessorExprEvaluatorTest {

    private fun eval(text: String, refs: Map<String, IsPreprocessorExprValue> = emptyMap()): IsPreprocessorExprValue? {
        val ast = IsPreprocessorExprParser.parse(text).ast
        return IsPreprocessorExprEvaluator(text, referenceValue = { refs[it] }).evaluate(ast)
    }

    @Test fun `integer literal`() = assertEquals(IntValue(42), eval("42"))

    @Test fun `arithmetic respects precedence`() = assertEquals(IntValue(14), eval("2 + 3 * 4"))

    @Test fun `parentheses change precedence`() = assertEquals(IntValue(20), eval("(2 + 3) * 4"))

    @Test fun `string concatenation`() = assertEquals(StrValue("abcd"), eval("\"ab\" + \"cd\""))

    @Test fun `doubled quotes unescape`() = assertEquals(StrValue("a\"b"), eval("\"a\"\"b\""))

    @Test fun `reference is substituted`() =
        assertEquals(IntValue(10), eval("x + 1", mapOf("x" to IntValue(9))))

    @Test fun `comparison yields 0 or 1`() {
        assertEquals(IntValue(1), eval("3 > 2"))
        assertEquals(IntValue(0), eval("3 < 2"))
    }

    @Test fun `unary minus`() = assertEquals(IntValue(-5), eval("-5"))

    @Test fun `division by zero is not computable`() = assertNull(eval("1 / 0"))

    @Test fun `function call is not computable`() = assertNull(eval("Len(\"x\")"))

    @Test fun `brace constant is not computable`() = assertNull(eval("{app}"))

    @Test fun `unresolved reference is not computable`() = assertNull(eval("y + 1"))

    @Test fun `mixing string and int is not computable`() = assertNull(eval("\"a\" + 1"))

    // ── value resolver (references between defines) ───────────────────────────

    @Test fun `value resolves transitively through defines`() {
        val r = IsPreprocessorExprValueResolver(
            listOf(
                IsPreprocessorExprDefineInfo("A", "10", 0),
                IsPreprocessorExprDefineInfo("B", "A + 5", 1),
            )
        )
        assertEquals(IntValue(15), r.evaluate("B", 2))
    }

    @Test fun `forward reference has no value`() {
        val r = IsPreprocessorExprValueResolver(listOf(IsPreprocessorExprDefineInfo("Later", "1", 1)))
        assertNull(r.valueOfReference("Later", beforeOrder = 0))
    }

    @Test fun `self reference terminates without value`() {
        val r = IsPreprocessorExprValueResolver(listOf(IsPreprocessorExprDefineInfo("P", "P + 1", 0)))
        assertNull(r.valueOfReference("P", beforeOrder = 5))
    }

    @Test fun `function macro call is evaluated by substituting the argument`() {
        // #define f(x) "a" + x  /  f("b") = "ab"
        val r = IsPreprocessorExprValueResolver(
            emptyList(),
            listOf(IsPreprocessorExprFunctionMacroInfo("f", listOf("x"), "\"a\" + x", 0)),
        )
        assertEquals(StrValue("ab"), r.evaluate("f(\"b\")", 1))
    }

    @Test fun `value flows through references and a macro call`() {
        // #define demo "test" / #define lang "demo.isl" / #define anyany(x) lang + x
        // #define hello demo + anyany("y")  →  "test" + ("demo.isl" + "y") = "testdemo.isly"
        val r = IsPreprocessorExprValueResolver(
            listOf(
                IsPreprocessorExprDefineInfo("demo", "\"test\"", 0),
                IsPreprocessorExprDefineInfo("lang", "\"demo.isl\"", 1),
                IsPreprocessorExprDefineInfo("hello", "demo + anyany(\"y\")", 3),
            ),
            listOf(IsPreprocessorExprFunctionMacroInfo("anyany", listOf("x"), "lang + x", 2)),
        )
        assertEquals(StrValue("testdemo.isly"), r.valueOfReference("hello", beforeOrder = 4))
    }

    @Test fun `macro call with a non-computable argument has no value`() {
        val r = IsPreprocessorExprValueResolver(
            emptyList(),
            listOf(IsPreprocessorExprFunctionMacroInfo("f", listOf("x"), "\"a\" + x", 0)),
        )
        assertNull(r.evaluate("f(Unknown)", 1))
    }
}
