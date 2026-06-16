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
            functionCallType = { name, _ -> funcs[name] ?: IsPreprocessorExprType.ANY },
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
    fun `any operand suppresses plus and comparison errors`() {
        // `+` and comparisons stay permissive against an unresolved operand.
        assertEquals(0, analyse("\"a\" + B", refs = mapOf("B" to IsPreprocessorExprType.ANY)).errors.size)
        assertEquals(0, analyse("\"a\" < B", refs = mapOf("B" to IsPreprocessorExprType.ANY)).errors.size)
    }

    @Test
    fun `concrete string with non-plus operator is an error even against an any operand`() {
        // A concrete string operand is never valid with `*`, even when the other side is unresolved
        // (e.g. a macro parameter): `#define func(x) "abc" * x`.
        assertEquals(1, analyse("\"abc\" * x", refs = mapOf("x" to IsPreprocessorExprType.ANY)).errors.size)
        assertEquals(1, analyse("x * \"abc\"", refs = mapOf("x" to IsPreprocessorExprType.ANY)).errors.size)
    }

    @Test
    fun `brace constant operand is any and suppresses errors`() {
        assertEquals(0, errorCount("{app} * 2"))
    }

    // ── function-like macro used without arguments ───────────────────────────

    /** A single function-like macro `func` declaring [arity] parameters. */
    private fun withMacro(text: String, arity: Int): IsPreprocessorExprTypeInference {
        val ast = IsPreprocessorExprParser.parse(text).ast
        val inference = IsPreprocessorExprTypeInference(
            functionMacroArity = { if (it.equals("func", ignoreCase = true)) arity else null },
        )
        inference.infer(ast)
        return inference
    }

    @Test
    fun `bare reference to a function-like macro is an error`() {
        assertEquals(1, withMacro("func", arity = 1).errors.size)
    }

    @Test
    fun `calling a function-like macro with the right argument count is fine`() {
        assertEquals(0, withMacro("func(1)", arity = 1).errors.size)
        assertEquals(0, withMacro("func(1, 2)", arity = 2).errors.size)
    }

    @Test
    fun `calling a function-like macro with too few arguments is an error`() {
        assertEquals(1, withMacro("func(1)", arity = 2).errors.size)
    }

    @Test
    fun `calling a function-like macro with too many arguments is an error`() {
        assertEquals(1, withMacro("func(1, 2, 3)", arity = 2).errors.size)
    }

    @Test
    fun `calling a parameterless function-like macro with no arguments is fine`() {
        assertEquals(0, withMacro("func()", arity = 0).errors.size)
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
