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
import org.junit.Test

/**
 * Verifies the recursive name resolution of [IsPreprocessorExprTypeResolver]: multi-level resolution, the
 * declaration-order rule (forward references don't resolve), cycle protection and memoization.
 */
class IsPreprocessorExprTypeResolverTest {

    private fun define(order: Int, name: String, expr: String) =
        IsPreprocessorExprDefineInfo(name, expr, order)

    private fun macro(order: Int, name: String, parameters: List<String>, body: String) =
        IsPreprocessorExprFunctionMacroInfo.ofNames(name, parameters, body, order)

    private fun typedMacro(
        order: Int,
        name: String,
        parameters: List<IsPreprocessorMacroParameter>,
        body: String,
    ) = IsPreprocessorExprFunctionMacroInfo(name, parameters, body, order)

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

    // ── probable parameter / return types (for documentation) ────────────────

    @Test
    fun `probable parameter and return type are int when the body forces int`() {
        val r = resolver(functionMacros = listOf(macro(0, "twice", listOf("x"), "x * 2")))
        assertEquals(listOf(IsPreprocessorExprType.INT), r.probableMacroParameterTypes("twice"))
        assertEquals(IsPreprocessorExprType.INT, r.probableMacroReturnType("twice"))
    }

    @Test
    fun `probable parameter and return type are str when the body forces str`() {
        val r = resolver(functionMacros = listOf(macro(0, "f", listOf("x"), "\"abc\" + x")))
        assertEquals(listOf(IsPreprocessorExprType.STR), r.probableMacroParameterTypes("f"))
        assertEquals(IsPreprocessorExprType.STR, r.probableMacroReturnType("f"))
    }

    @Test
    fun `unconstrained parameter and return type are any`() {
        val r = resolver(functionMacros = listOf(macro(0, "id", listOf("x"), "x")))
        assertEquals(listOf(IsPreprocessorExprType.ANY), r.probableMacroParameterTypes("id"))
        assertEquals(IsPreprocessorExprType.ANY, r.probableMacroReturnType("id"))
    }

    @Test
    fun `calling a function macro with an ill-typed argument is an error`() {
        // twice(x) = x * 2 constrains x to int; passing a string is a type error.
        val r = resolver(functionMacros = listOf(macro(0, "twice", listOf("x"), "x * 2")))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("twice(\"a\")").ast)
        assertEquals(1, inference.errors.size)
    }

    @Test
    fun `calling a function macro with a well-typed argument is fine`() {
        val r = resolver(functionMacros = listOf(macro(0, "twice", listOf("x"), "x * 2")))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("twice(5)").ast)
        assertEquals(0, inference.errors.size)
    }

    /** A declared parameter type wins over the type probed from the body, so `any x` stays unconstrained. */
    @Test
    fun `declared parameter type overrides the probed one`() {
        val r = resolver(
            functionMacros = listOf(
                typedMacro(
                    0, "twice",
                    listOf(IsPreprocessorMacroParameter("x", IsPreprocessorExprType.ANY)),
                    "x * 2",
                )
            )
        )
        assertEquals(listOf(IsPreprocessorExprType.ANY), r.probableMacroParameterTypes("twice"))
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("twice(\"a\")").ast)
        assertEquals("An explicitly declared 'any' must accept a string", 0, inference.errors.size)
    }

    /** A declared type constrains an argument the body alone would not, e.g. `#define pass(int n) n`. */
    @Test
    fun `declared parameter type constrains the argument`() {
        val r = resolver(
            functionMacros = listOf(
                typedMacro(
                    0, "pass",
                    listOf(IsPreprocessorMacroParameter("n", IsPreprocessorExprType.INT)),
                    "n",
                )
            )
        )
        val inference = r.inferenceAt(1)
        inference.infer(IsPreprocessorExprParser.parse("pass(\"a\")").ast)
        assertEquals("A declared int parameter must reject a string", 1, inference.errors.size)
    }

    /** A parameter with a default value may be omitted, but the mandatory ones may not. */
    @Test
    fun `default value makes a parameter optional`() {
        val r = resolver(
            functionMacros = listOf(
                typedMacro(
                    0, "mul",
                    listOf(
                        IsPreprocessorMacroParameter("a", IsPreprocessorExprType.INT),
                        IsPreprocessorMacroParameter("b", IsPreprocessorExprType.INT, defaultValue = "10"),
                    ),
                    "a * b",
                )
            )
        )
        val withDefault = r.inferenceAt(1)
        withDefault.infer(IsPreprocessorExprParser.parse("mul(2)").ast)
        assertEquals("The optional argument may be omitted", 0, withDefault.errors.size)

        val withoutMandatory = r.inferenceAt(1)
        withoutMandatory.infer(IsPreprocessorExprParser.parse("mul()").ast)
        assertEquals("The mandatory argument is still required", 1, withoutMandatory.errors.size)
    }

    /** A by-reference parameter needs a bare macro name it can write back into. */
    @Test
    fun `by reference parameter requires a macro name`() {
        val r = resolver(
            define(0, "target", "0"),
            functionMacros = listOf(
                typedMacro(
                    1, "fill",
                    listOf(IsPreprocessorMacroParameter("out", IsPreprocessorExprType.INT, byRef = true)),
                    "out",
                )
            )
        )
        val literal = r.inferenceAt(2)
        literal.infer(IsPreprocessorExprParser.parse("fill(1)").ast)
        assertEquals("A literal must be rejected in a by-reference position", 1, literal.errors.size)

        val name = r.inferenceAt(2)
        name.infer(IsPreprocessorExprParser.parse("fill(target)").ast)
        assertEquals("A macro name is a valid by-reference argument", 0, name.errors.size)
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
