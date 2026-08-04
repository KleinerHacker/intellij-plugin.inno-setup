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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the [IsPreprocessorExprTokenizer]: every operator (single- and multi-character) with longest
 * match, numbers/strings/identifiers/constants, exact offsets and whitespace/empty handling.
 */
class IsPreprocessorExprTokenizerTest {

    private fun tokenize(text: String) = IsPreprocessorExprTokenizer.tokenize(text)

    private fun types(text: String) = tokenize(text).map { it.type }

    private fun texts(text: String) = tokenize(text).map { it.text }

    @Test
    fun `empty input yields no tokens`() {
        assertTrue(tokenize("").isEmpty())
    }

    @Test
    fun `whitespace only yields no tokens`() {
        assertTrue(tokenize("   \t  ").isEmpty())
    }

    @Test
    fun `single character operators are recognised`() {
        for (op in listOf("+", "-", "*", "/", "%", "&", "|", "^", "~", "!", "<", ">")) {
            val tokens = tokenize("1 $op 2").filter { it.type == IsPreprocessorExprTokenType.OPERATOR }
            assertEquals("operator '$op'", 1, tokens.size)
            assertEquals(op, tokens.first().text)
        }
    }

    @Test
    fun `multi character operators win over single prefixes (longest match)`() {
        for (op in listOf("<<", ">>", "<=", ">=", "==", "!=", "&&", "||")) {
            val tokens = tokenize("1 $op 2").filter { it.type == IsPreprocessorExprTokenType.OPERATOR }
            assertEquals("operator '$op'", 1, tokens.size)
            assertEquals(op, tokens.first().text)
        }
    }

    @Test
    fun `double shift is one token not two angle brackets`() {
        assertEquals(listOf("a", "<<", "b"), texts("a << b"))
    }

    @Test
    fun `number literal including dotted form`() {
        assertEquals(
            listOf(IsPreprocessorExprTokenType.NUMBER),
            types("1.2.3"),
        )
        assertEquals("1.2.3", texts("1.2.3").first())
    }

    @Test
    fun `string literal with double-quote escape is one token`() {
        val tokens = tokenize("\"Builds\\\\\"")
        assertEquals(1, tokens.size)
        assertEquals(IsPreprocessorExprTokenType.STRING, tokens.first().type)
    }

    @Test
    fun `single quoted string is one token`() {
        val tokens = tokenize("'abc'")
        assertEquals(1, tokens.size)
        assertEquals(IsPreprocessorExprTokenType.STRING, tokens.first().type)
    }

    @Test
    fun `doubled quote inside string does not terminate it`() {
        val tokens = tokenize("\"a\"\"b\"")
        assertEquals(1, tokens.size)
        assertEquals("\"a\"\"b\"", tokens.first().text)
    }

    @Test
    fun `brace constant is a single atom token`() {
        val tokens = tokenize("{app}")
        assertEquals(1, tokens.size)
        assertEquals(IsPreprocessorExprTokenType.CONSTANT, tokens.first().type)
    }

    @Test
    fun `nested braces in constant are balanced`() {
        val tokens = tokenize("{a{b}c}")
        assertEquals(1, tokens.size)
        assertEquals("{a{b}c}", tokens.first().text)
    }

    @Test
    fun `identifier and parentheses and comma`() {
        assertEquals(
            listOf(
                IsPreprocessorExprTokenType.IDENT,
                IsPreprocessorExprTokenType.LPAREN,
                IsPreprocessorExprTokenType.IDENT,
                IsPreprocessorExprTokenType.COMMA,
                IsPreprocessorExprTokenType.IDENT,
                IsPreprocessorExprTokenType.RPAREN,
            ),
            types("Max(a, b)"),
        )
    }

    @Test
    fun `question and colon are own token types`() {
        assertEquals(
            listOf(
                IsPreprocessorExprTokenType.IDENT,
                IsPreprocessorExprTokenType.QUESTION,
                IsPreprocessorExprTokenType.NUMBER,
                IsPreprocessorExprTokenType.COLON,
                IsPreprocessorExprTokenType.NUMBER,
            ),
            types("a ? 1 : 2"),
        )
    }

    /**
     * A directive may be continued on the next line with a trailing backslash; the backslash and the line
     * break carry no meaning for the expression and must be skipped like whitespace.
     */
    @Test
    fun `line continuation is skipped like whitespace`() {
        assertEquals(
            listOf(
                IsPreprocessorExprTokenType.IDENT,
                IsPreprocessorExprTokenType.OPERATOR,
                IsPreprocessorExprTokenType.IDENT,
            ),
            types("a + \\\n    b"),
        )
    }

    @Test
    fun `token offsets address the exact source range`() {
        // 0123456
        // a + bc
        val tokens = tokenize("a + bc")
        assertEquals(0, tokens[0].start); assertEquals(1, tokens[0].end)
        assertEquals(2, tokens[1].start); assertEquals(3, tokens[1].end)
        assertEquals(4, tokens[2].start); assertEquals(6, tokens[2].end)
    }
}
