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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression

import org.junit.Assert.*
import org.junit.Test

/**
 * Verifies array support in the ISPP expression engine: parsing of `arr\[index]`, type inference (non-array,
 * missing index, non-integer index) and — the focus — **static value resolution across `#dim`/`#define`** via
 * [IsPreprocessorExprValueResolver].
 */
class IsPreprocessorExprArrayTest {

    // ── parser ────────────────────────────────────────────────────────────────

    @Test
    fun `index access parses to an index node`() {
        val node = IsPreprocessorExprParser.parse("arr[0]").ast
        assertTrue(node is IsPreprocessorExprIndex)
        node as IsPreprocessorExprIndex
        assertEquals("arr", node.name)
        assertTrue(node.index is IsPreprocessorExprIntLiteral)
    }

    @Test
    fun `index expression may itself be an expression`() {
        val node = IsPreprocessorExprParser.parse("arr[i + 1]").ast as IsPreprocessorExprIndex
        assertTrue(node.index is IsPreprocessorExprBinary)
    }

    @Test
    fun `nested index access parses`() {
        val node = IsPreprocessorExprParser.parse("a[b[0]]").ast as IsPreprocessorExprIndex
        assertTrue(node.index is IsPreprocessorExprIndex)
    }

    @Test
    fun `unbalanced bracket is reported`() {
        val result = IsPreprocessorExprParser.parse("arr[0")
        assertTrue(result.errors.any { it.message.contains("]") })
    }

    @Test
    fun `index access is usable as an operand`() {
        val node = IsPreprocessorExprParser.parse("arr[0] + 1").ast as IsPreprocessorExprBinary
        assertEquals(IsPreprocessorExprBinaryOperator.PLUS, node.operator)
        assertTrue(node.left is IsPreprocessorExprIndex)
    }

    // ── type inference ──────────────────────────────────────────────────────────

    private fun inferErrors(text: String, arrays: Set<String> = emptySet()): List<IsPreprocessorExprError> {
        val inference = IsPreprocessorExprTypeInference(isArray = { it in arrays })
        inference.infer(IsPreprocessorExprParser.parse(text).ast)
        return inference.errors
    }

    @Test
    fun `indexing a non-array is an error`() {
        assertTrue(inferErrors("foo[0]", arrays = emptySet()).any { it.message.contains("not an array") })
    }

    @Test
    fun `array referenced without an index is an error`() {
        assertTrue(inferErrors("arr", arrays = setOf("arr")).any { it.message.contains("must be indexed") })
    }

    @Test
    fun `a valid index access produces no error`() {
        assertTrue(inferErrors("arr[0]", arrays = setOf("arr")).isEmpty())
    }

    @Test
    fun `a string index is an error`() {
        assertTrue(inferErrors("arr[\"x\"]", arrays = setOf("arr")).any { it.message.contains("must be an integer") })
    }

    // ── value resolution across #dim / #define (focus) ──────────────────────────

    private fun resolver(
        defines: List<IsPreprocessorExprDefineInfo> = emptyList(),
        elements: List<IsPreprocessorExprArrayElementInfo> = emptyList(),
    ) = IsPreprocessorExprValueResolver(defines, emptyList(), elements)

    @Test
    fun `assigned element resolves to its value`() {
        val r = resolver(elements = listOf(IsPreprocessorExprArrayElementInfo("a", 1, "42", 10)))
        assertEquals(IsPreprocessorExprValue.IntValue(42), r.arrayElementValue("a", 1, 100))
    }

    @Test
    fun `element expression referencing a scalar define resolves`() {
        val r = resolver(
            defines = listOf(IsPreprocessorExprDefineInfo("N", "7", 0)),
            elements = listOf(IsPreprocessorExprArrayElementInfo("a", 0, "N * 2", 10)),
        )
        assertEquals(IsPreprocessorExprValue.IntValue(14), r.arrayElementValue("a", 0, 100))
    }

    @Test
    fun `expression reading two elements resolves`() {
        val r = resolver(
            elements = listOf(
                IsPreprocessorExprArrayElementInfo("a", 0, "3", 10),
                IsPreprocessorExprArrayElementInfo("a", 1, "4", 20),
            ),
        )
        assertEquals(IsPreprocessorExprValue.IntValue(7), r.evaluate("a[0] + a[1]", 100))
    }

    @Test
    fun `a scalar define reading an array element resolves`() {
        val r = resolver(elements = listOf(IsPreprocessorExprArrayElementInfo("a", 0, "5", 10)))
        assertEquals(IsPreprocessorExprValue.IntValue(6), r.evaluate("a[0] + 1", 100))
    }

    @Test
    fun `cross-array element reference resolves`() {
        val r = resolver(
            elements = listOf(
                IsPreprocessorExprArrayElementInfo("a", 0, "3", 10),
                IsPreprocessorExprArrayElementInfo("c", 0, "a[0] + 4", 20),
            ),
        )
        assertEquals(IsPreprocessorExprValue.IntValue(7), r.arrayElementValue("c", 0, 100))
    }

    @Test
    fun `string elements concatenate`() {
        val r = resolver(
            elements = listOf(
                IsPreprocessorExprArrayElementInfo("s", 0, "\"x\"", 10),
                IsPreprocessorExprArrayElementInfo("s", 1, "\"y\"", 20),
            ),
        )
        assertEquals(IsPreprocessorExprValue.StrValue("xy"), r.evaluate("s[0] + s[1]", 100))
    }

    @Test
    fun `element read before its assignment is not computable`() {
        val r = resolver(elements = listOf(IsPreprocessorExprArrayElementInfo("a", 0, "5", 10)))
        assertNull(r.arrayElementValue("a", 0, 5))   // assignment order 10 is not < 5
    }

    @Test
    fun `unassigned element is not computable`() {
        val r = resolver(elements = listOf(IsPreprocessorExprArrayElementInfo("a", 0, "5", 10)))
        assertNull(r.arrayElementValue("a", 1, 100))
    }

    @Test
    fun `non-constant index is not computable`() {
        val r = resolver(elements = listOf(IsPreprocessorExprArrayElementInfo("a", 0, "5", 10)))
        assertNull(r.evaluate("a[i]", 100))   // i is unresolved
    }
}
