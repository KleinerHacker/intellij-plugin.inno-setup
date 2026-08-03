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

    /** A single function-like macro `func` declaring [arity] unconstrained (ANY) parameters. */
    private fun withMacro(text: String, arity: Int): IsPreprocessorExprTypeInference =
        withMacroParams(text, List(arity) { IsPreprocessorExprType.ANY })

    /** A single function-like macro `func` whose probable parameter types are [paramTypes]. */
    private fun withMacroParams(
        text: String,
        paramTypes: List<IsPreprocessorExprType>
    ): IsPreprocessorExprTypeInference {
        val ast = IsPreprocessorExprParser.parse(text).ast
        val inference = IsPreprocessorExprTypeInference(
            functionMacroParameterTypes = { if (it.equals("func", ignoreCase = true)) paramTypes else null },
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

    @Test
    fun `argument of the wrong type for a typed parameter is an error`() {
        // func expects an int parameter; passing a string is a type error.
        assertEquals(1, withMacroParams("func(\"a\")", listOf(IsPreprocessorExprType.INT)).errors.size)
        // … and the reverse: a str parameter, passing an int.
        assertEquals(1, withMacroParams("func(5)", listOf(IsPreprocessorExprType.STR)).errors.size)
    }

    @Test
    fun `argument of the matching type for a typed parameter is fine`() {
        assertEquals(0, withMacroParams("func(5)", listOf(IsPreprocessorExprType.INT)).errors.size)
        assertEquals(0, withMacroParams("func(\"a\")", listOf(IsPreprocessorExprType.STR)).errors.size)
    }

    @Test
    fun `any-typed parameter accepts any argument`() {
        assertEquals(0, withMacroParams("func(\"a\")", listOf(IsPreprocessorExprType.ANY)).errors.size)
        assertEquals(0, withMacroParams("func(5)", listOf(IsPreprocessorExprType.ANY)).errors.size)
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

    // ── built-in signature validation ────────────────────────────────────────

    /** Infers [text] with the given built-in [signatures] and the given declared [arrays] in scope. */
    private fun withBuiltins(
        text: String,
        vararg signatures: String,
        arrays: Set<String> = emptySet(),
    ): IsPreprocessorExprTypeInference {
        val parsed = signatures.mapNotNull { parseBuiltinSignature(it) }.associateBy { it.name.lowercase() }
        val ast = IsPreprocessorExprParser.parse(text).ast
        val inference = IsPreprocessorExprTypeInference(
            functionCallType = { name, _ ->
                parsed[name.lowercase()]?.returnType ?: IsPreprocessorExprType.ANY
            },
            isArray = { name -> arrays.any { it.equals(name, ignoreCase = true) } },
            builtinSignature = { parsed[it.lowercase()] },
        )
        inference.infer(ast)
        return inference
    }

    private val copySignature = "Copy(S: str, Index: int, Count: int): str"
    private val findSignature = "Find(S: str, Substr: str, Index: int = 1): int"
    private val errorSignature = "Error(Message: str): void"

    /** A built-in call with exactly the declared arguments produces no diagnostic. */
    @Test
    fun `builtin call with matching arguments is fine`() {
        assertEquals(0, withBuiltins("Copy(\"abc\", 1, 2)", copySignature).errors.size)
    }

    /** Fewer arguments than the built-in requires is an error. */
    @Test
    fun `builtin call with too few arguments is an error`() {
        val errors = withBuiltins("Copy(\"abc\")", copySignature).errors
        assertEquals(1, errors.size)
        assertTrue(errors[0].message.contains("3 arguments"))
    }

    /** More arguments than the built-in accepts is an error. */
    @Test
    fun `builtin call with too many arguments is an error`() {
        assertEquals(1, withBuiltins("Copy(\"abc\", 1, 2, 3)", copySignature).errors.size)
    }

    /** A parameter with a default value may be omitted, but the required ones may not. */
    @Test
    fun `optional builtin parameters may be omitted`() {
        assertEquals(0, withBuiltins("Find(\"abc\", \"b\")", findSignature).errors.size)
        assertEquals(0, withBuiltins("Find(\"abc\", \"b\", 2)", findSignature).errors.size)
        assertEquals(1, withBuiltins("Find(\"abc\")", findSignature).errors.size)
    }

    /** Passing a string where the built-in declares an integer parameter is an error. */
    @Test
    fun `builtin argument of the wrong type is an error`() {
        val errors = withBuiltins("Copy(\"abc\", \"x\", 2)", copySignature).errors
        assertEquals(1, errors.size)
        assertTrue(errors[0].message.contains("Index"))
    }

    /** An `any` parameter accepts every argument type. */
    @Test
    fun `any parameter of a builtin accepts every type`() {
        val signature = "Str(Value: any): str"
        assertEquals(0, withBuiltins("Str(1)", signature).errors.size)
        assertEquals(0, withBuiltins("Str(\"a\")", signature).errors.size)
    }

    /** A by-reference parameter must receive a bare macro name it can write back into. */
    @Test
    fun `by reference argument must be a macro name`() {
        val signature = "StringChange(S: str*, FromStr: str, ToStr: str): int"
        assertEquals(0, withBuiltins("StringChange(MyVar, \"a\", \"b\")", signature).errors.size)
        assertEquals(1, withBuiltins("StringChange(\"lit\", \"a\", \"b\")", signature).errors.size)
    }

    /** An `Ident` parameter takes an un-evaluated symbol name, so an undefined name is not an error. */
    @Test
    fun `ident parameter accepts an undefined symbol name`() {
        assertEquals(0, withBuiltins("Defined(Unknown)", "Defined(Ident): int").errors.size)
    }

    /** An `Ident` parameter rejects anything that is not a bare identifier. */
    @Test
    fun `ident parameter rejects a literal`() {
        assertEquals(1, withBuiltins("Defined(\"x\")", "Defined(Ident): int").errors.size)
    }

    /** An `Array` parameter takes the array name without indexing it. */
    @Test
    fun `array parameter accepts a declared array name`() {
        assertEquals(
            0,
            withBuiltins("DimOf(Arr)", "DimOf(Array): int", arrays = setOf("Arr")).errors.size,
        )
    }

    /** An `Array` parameter rejects a name that is not a declared array. */
    @Test
    fun `array parameter rejects a non array name`() {
        assertEquals(1, withBuiltins("DimOf(NotAnArray)", "DimOf(Array): int").errors.size)
    }

    /** A void built-in used as a value inside an expression is an error. */
    @Test
    fun `void builtin used as a value is an error`() {
        val errors = withBuiltins("Error(\"boom\") + 1", errorSignature).errors
        assertTrue(errors.any { it.message.contains("returns no value") })
    }

    /** A void built-in called as the whole expression is legitimate and produces no diagnostic. */
    @Test
    fun `void builtin as the whole expression is fine`() {
        assertEquals(0, withBuiltins("Error(\"boom\")", errorSignature).errors.size)
    }

    /** A void built-in passed as an argument of another call is an error. */
    @Test
    fun `void builtin as an argument is an error`() {
        val errors = withBuiltins("Copy(Error(\"x\"), 1, 2)", copySignature, errorSignature).errors
        assertTrue(errors.any { it.message.contains("returns no value") })
    }

    /** Nested built-in calls are validated on every level. */
    @Test
    fun `nested builtin calls are validated`() {
        val errors = withBuiltins("Copy(Copy(\"abc\"), 1, 2)", copySignature).errors
        assertEquals(1, errors.size)
    }

    /** A call to a name without a known signature stays permissive (unknown names are the annotator's job). */
    @Test
    fun `call without a known signature is not checked`() {
        assertEquals(0, withBuiltins("Whatever(1, 2, 3)").errors.size)
    }
}
