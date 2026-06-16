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
 * Verifies the pure type rules of [IsPreprocessorExprTypeInference] (without recursive name resolution):
 * each operator category, `void` compatibility, `ANY` suppression and built-in return-type propagation.
 */
class IsPreprocessorExprTypeInferenceTest {

    /** Infers [text]; references/functions resolve via the supplied maps (default → ANY). */
    private fun analyse(
        text: String,
        refs: Map<String, IsPreprocessorExprType> = emptyMap(),
        funcs: Map<String, IsPreprocessorExprType> = emptyMap(),
    ): IsPreprocessorExprTypeInference {
        val ast = IsPreprocessorExprParser.parse(text).ast
        val inference = IsPreprocessorExprTypeInference(
            referenceType = { refs[it] ?: IsPreprocessorExprType.ANY },
            functionReturnType = { funcs[it] ?: IsPreprocessorExprType.ANY },
        )
        inference.infer(ast)
        return inference
    }

    private fun typeOf(text: String) = run {
        val ast = IsPreprocessorExprParser.parse(text).ast
        IsPreprocessorExprTypeInference().infer(ast)
    }

    private fun errorCount(text: String) = analyse(text).errors.size

    // ── + : addition vs concatenation ────────────────────────────────────────

    @Test
    fun `int plus int is int`() {
        assertEquals(IsPreprocessorExprType.INT, typeOf("1 + 2"))
        assertEquals(0, errorCount("1 + 2"))
    }

    @Test
    fun `string plus string is str`() {
        assertEquals(IsPreprocessorExprType.STR, typeOf("\"a\" + \"b\""))
        assertEquals(0, errorCount("\"a\" + \"b\""))
    }

    @Test
    fun `int plus string is an error`() {
        assertEquals(1, errorCount("1 + \"s\""))
    }

    @Test
    fun `string plus int is an error`() {
        assertEquals(1, errorCount("\"s\" + 1"))
    }

    // ── arithmetic / bitwise require integers ────────────────────────────────

    @Test
    fun `multiplying strings is an error`() {
        assertEquals(1, errorCount("\"a\" * \"b\""))
    }

    @Test
    fun `integer multiplication is fine`() {
        assertEquals(0, errorCount("2 * 3"))
    }

    @Test
    fun `string in arithmetic and bitwise is an error`() {
        for (op in listOf("-", "*", "/", "%", "&", "|", "^", "<<", ">>")) {
            assertEquals("operator '$op'", 1, errorCount("\"a\" $op 1"))
        }
    }

    // ── comparison ───────────────────────────────────────────────────────────

    @Test
    fun `comparing two integers or two strings is fine`() {
        assertEquals(0, errorCount("1 < 2"))
        assertEquals(0, errorCount("\"a\" < \"b\""))
    }

    @Test
    fun `comparing a string with an integer is an error`() {
        assertEquals(1, errorCount("\"a\" < 1"))
        assertEquals(1, errorCount("1 == \"a\""))
    }

    // ── unary ────────────────────────────────────────────────────────────────

    @Test
    fun `unary minus on a string is an error`() {
        assertEquals(1, errorCount("-\"s\""))
    }

    @Test
    fun `unary minus on an integer is fine`() {
        assertEquals(0, errorCount("-5"))
    }

    // ── void compatibility ───────────────────────────────────────────────────

    @Test
    fun `empty operand is void and compatible everywhere`() {
        // An empty expression is VOID; VOID combines with both int and str without error.
        assertEquals(IsPreprocessorExprType.VOID, typeOf(""))
    }

    // ── ANY suppression ──────────────────────────────────────────────────────

    @Test
    fun `any operand suppresses type errors`() {
        assertEquals(0, analyse("A * \"b\"", refs = mapOf("A" to IsPreprocessorExprType.ANY)).errors.size)
        assertEquals(0, analyse("\"a\" + B", refs = mapOf("B" to IsPreprocessorExprType.ANY)).errors.size)
    }

    @Test
    fun `brace constant operand is any and suppresses errors`() {
        assertEquals(0, errorCount("{app} * 2"))
    }

    // ── built-in return types ────────────────────────────────────────────────

    @Test
    fun `string returning builtin concatenates without error`() {
        val inference = analyse("Str(x) + \"y\"", funcs = mapOf("Str" to IsPreprocessorExprType.STR))
        assertEquals(0, inference.errors.size)
    }

    @Test
    fun `string returning builtin in multiplication is an error`() {
        val inference = analyse("Str(x) * 2", funcs = mapOf("Str" to IsPreprocessorExprType.STR))
        assertEquals(1, inference.errors.size)
    }

    @Test
    fun `int returning builtin in arithmetic is fine`() {
        val inference = analyse("Power(2, 3) + 1", funcs = mapOf("Power" to IsPreprocessorExprType.INT))
        assertEquals(0, inference.errors.size)
    }
}
