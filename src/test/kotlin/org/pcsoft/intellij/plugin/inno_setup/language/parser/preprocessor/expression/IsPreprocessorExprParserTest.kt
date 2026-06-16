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

/**
 * Verifies the [IsPreprocessorExprParser]: precedence/associativity, parentheses, calls, ternary and the
 * exact ranges of structural (syntax) errors.
 */
class IsPreprocessorExprParserTest {

    private fun parse(text: String) = IsPreprocessorExprParser.parse(text)

    /** Substring of [text] addressed by an error span. */
    private fun IsPreprocessorExprError.slice(text: String) = text.substring(span.start, span.end)

    @Test
    fun `empty expression parses to the empty node without errors`() {
        val result = parse("")
        assertTrue(result.ast is IsPreprocessorExprEmpty)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `multiplication binds tighter than addition`() {
        val result = parse("1 + 2 * 3")
        val root = result.ast as IsPreprocessorExprBinary
        assertEquals(IsPreprocessorExprBinaryOperator.PLUS, root.operator)
        assertTrue("right side must be the multiplication", root.right is IsPreprocessorExprBinary)
        assertEquals(
            IsPreprocessorExprBinaryOperator.MUL,
            (root.right as IsPreprocessorExprBinary).operator,
        )
    }

    @Test
    fun `addition is left associative`() {
        val result = parse("1 - 2 - 3")
        val root = result.ast as IsPreprocessorExprBinary
        assertTrue("left side must itself be a subtraction", root.left is IsPreprocessorExprBinary)
    }

    @Test
    fun `parentheses override precedence`() {
        val result = parse("(1 + 2) * 3")
        val root = result.ast as IsPreprocessorExprBinary
        assertEquals(IsPreprocessorExprBinaryOperator.MUL, root.operator)
        assertTrue(root.left is IsPreprocessorExprParen)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `unary minus parses and nests`() {
        val result = parse("- -5")
        val outer = result.ast as IsPreprocessorExprUnary
        assertEquals(IsPreprocessorExprUnaryOperator.MINUS, outer.operator)
        assertTrue(outer.operand is IsPreprocessorExprUnary)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `function call with multiple arguments`() {
        val result = parse("Max(a, b)")
        val call = result.ast as IsPreprocessorExprCall
        assertEquals("Max", call.name)
        assertEquals(2, call.arguments.size)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `nested call argument`() {
        val result = parse("Str(Major)")
        val call = result.ast as IsPreprocessorExprCall
        assertEquals(1, call.arguments.size)
        assertTrue(call.arguments.first() is IsPreprocessorExprReference)
    }

    @Test
    fun `ternary parses`() {
        val result = parse("a > b ? a : b")
        assertTrue(result.ast is IsPreprocessorExprTernary)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `missing operator flags the second operand`() {
        val result = parse("5 6")
        assertEquals(1, result.errors.size)
        assertEquals("6", result.errors.first().slice("5 6"))
    }

    @Test
    fun `missing operand reports an error`() {
        val result = parse("1 +")
        assertTrue(result.errors.any { it.message.contains("Operand expected") })
    }

    @Test
    fun `unbalanced opening parenthesis flags the bracket`() {
        val text = "(1 + 2"
        val result = parse(text)
        val error = result.errors.firstOrNull { it.message.contains("Unbalanced") }
        assertNotNull(error)
        assertEquals("(", error!!.slice(text))
    }

    @Test
    fun `trailing closing parenthesis is reported`() {
        val result = parse("1 + 2)")
        assertTrue(result.errors.isNotEmpty())
    }
}
