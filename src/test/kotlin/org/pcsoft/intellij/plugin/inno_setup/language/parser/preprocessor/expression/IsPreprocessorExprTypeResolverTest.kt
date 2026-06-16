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
 * Verifies the recursive name resolution of [IsPreprocessorExprTypeResolver]: multi-level resolution, the
 * declaration-order rule (forward references don't resolve), cycle protection and memoization.
 */
class IsPreprocessorExprTypeResolverTest {

    private fun define(order: Int, name: String, expr: String) =
        IsPreprocessorExprDefineInfo(name, expr, order)

    private fun macro(order: Int, name: String, parameters: List<String>, body: String) =
        IsPreprocessorExprFunctionMacroInfo(name, parameters, body, order)

    private fun resolver(
        vararg defines: IsPreprocessorExprDefineInfo,
        functionMacros: List<IsPreprocessorExprFunctionMacroInfo> = emptyList(),
        variableType: (String) -> IsPreprocessorExprType? = { null },
        builtinReturnType: (String) -> IsPreprocessorExprType = { IsPreprocessorExprType.ANY },
    ) = IsPreprocessorExprTypeResolver(defines.toList(), functionMacros, variableType, builtinReturnType)

    @Test
    fun `simple reference resolves to the referenced define's type`() {
        val r = resolver(define(0, "A", "\"x\""))
        assertEquals(IsPreprocessorExprType.STR, r.typeOfReference("A", beforeOrder = 1))
    }

    @Test
    fun `reference resolution is case-insensitive`() {
        val r = resolver(define(0, "A", "5"))
        assertEquals(IsPreprocessorExprType.INT, r.typeOfReference("a", beforeOrder = 1))
    }

    @Test
    fun `multi-level chain resolves transitively`() {
        // C → B → A = int
        val r = resolver(
            define(0, "A", "5"),
            define(1, "B", "A"),
            define(2, "C", "B"),
        )
        assertEquals(IsPreprocessorExprType.INT, r.typeOfReference("C", beforeOrder = 3))
    }

    @Test
    fun `str times int through references produces a type error`() {
        // #define A "x" / #define B 5 / #define C A * B  → str * int
        val r = resolver(
            define(0, "A", "\"x\""),
            define(1, "B", "5"),
        )
        val inference = r.inferenceAt(2)
        inference.infer(IsPreprocessorExprParser.parse("A * B").ast)
        assertEquals(1, inference.errors.size)
    }

    @Test
    fun `forward reference does not resolve (declaration-order rule)`() {
        // Referencing a #define declared *after* the current line yields ANY (no false error).
        val r = resolver(
            define(1, "Later", "\"x\""),
        )
        assertEquals(IsPreprocessorExprType.ANY, r.typeOfReference("Later", beforeOrder = 0))
    }

    @Test
    fun `self reference does not recurse forever`() {
        // #define P P + 1 (out of any sane order) must terminate with ANY.
        val r = resolver(define(0, "P", "P + 1"))
        assertEquals(IsPreprocessorExprType.ANY, r.typeOfReference("P", beforeOrder = 5))
    }

    @Test
    fun `mutual cycle terminates with ANY`() {
        // Out-of-order mutual references A↔B; the cycle guard must break recursion.
        val r = resolver(
            define(0, "A", "B"),
            define(1, "B", "A"),
        )
        // Reference from a later line; both resolve to each other → guard returns ANY, no stack overflow.
        assertEquals(IsPreprocessorExprType.ANY, r.typeOfReference("B", beforeOrder = 5))
    }

    @Test
    fun `predefined variable type takes precedence`() {
        val r = resolver(variableType = { if (it == "__LINE__") IsPreprocessorExprType.INT else null })
        assertEquals(IsPreprocessorExprType.INT, r.typeOfReference("__LINE__", beforeOrder = 0))
    }

    @Test
    fun `unknown identifier yields ANY`() {
        val r = resolver()
        assertEquals(IsPreprocessorExprType.ANY, r.typeOfReference("Nope", beforeOrder = 10))
    }

    @Test
    fun `latest define before the line wins`() {
        // Two #defines of the same name; the most recent one before the reference is used.
        val r = resolver(
            define(0, "X", "5"),
            define(1, "X", "\"s\""),
        )
        assertEquals(IsPreprocessorExprType.STR, r.typeOfReference("X", beforeOrder = 2))
        assertEquals(IsPreprocessorExprType.INT, r.typeOfReference("X", beforeOrder = 1))
    }

    @Test
    fun `builtin return type flows into reference resolution`() {
        val r = resolver(
            define(0, "V", "Str(5)"),
            builtinReturnType = { if (it == "Str") IsPreprocessorExprType.STR else IsPreprocessorExprType.ANY },
        )
        assertEquals(IsPreprocessorExprType.STR, r.typeOfReference("V", beforeOrder = 1))
    }

    // ── function-like macro return-type inference ─────────────────────────────

    @Test
    fun `function macro return type flows into a call producing a type error`() {
        // #define func(x) "abc" + x / #define intVar 10 / #define myVar func("x") + intVar
        // func("x") is str → str + int is an error.
        val r = resolver(
            define(1, "intVar", "10"),
            functionMacros = listOf(macro(0, "func", listOf("x"), "\"abc\" + x")),
        )
        val inference = r.inferenceAt(2)
        inference.infer(IsPreprocessorExprParser.parse("func(\"x\") + intVar").ast)
        assertEquals(1, inference.errors.size)
    }

    @Test
    fun `function macro returning str concatenated with str is fine`() {
        val r = resolver(functionMacros = listOf(macro(0, "func", listOf("x"), "\"abc\" + x")))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("func(\"x\") + \"y\"").ast)
        assertEquals(0, inference.errors.size)
    }

    @Test
    fun `function macro returning int used in arithmetic is fine`() {
        val r = resolver(functionMacros = listOf(macro(0, "twice", listOf("x"), "x * 2")))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("twice(5) + 1").ast)
        assertEquals(0, inference.errors.size)
    }

    @Test
    fun `function macro return type depends on the argument type`() {
        // The same macro yields str for a string argument (→ error against int) …
        val r = resolver(
            define(1, "n", "10"),
            functionMacros = listOf(macro(0, "id", listOf("x"), "x")),
        )
        val strCall = r.inferenceAt(2)
        strCall.infer(IsPreprocessorExprParser.parse("id(\"a\") * n").ast)
        assertEquals(1, strCall.errors.size)

        // … and int for an integer argument (→ no error).
        val intCall = r.inferenceAt(2)
        intCall.infer(IsPreprocessorExprParser.parse("id(5) * n").ast)
        assertEquals(0, intCall.errors.size)
    }

    @Test
    fun `recursive function macro terminates`() {
        val r = resolver(functionMacros = listOf(macro(0, "rec", listOf("x"), "rec(x) + 1")))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("rec(1)").ast)
        // The cycle guard must break recursion; the result is permissive (no stack overflow / false error).
        assertEquals(0, inference.errors.size)
    }
}
