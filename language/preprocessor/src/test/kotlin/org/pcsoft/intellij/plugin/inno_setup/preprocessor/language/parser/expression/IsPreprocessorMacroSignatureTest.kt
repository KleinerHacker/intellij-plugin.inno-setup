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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the parsing of an ISPP function-like macro parameter list
 * (`[<type>] [*] <name> [= <default>]`, comma separated) into [IsPreprocessorMacroParameter] and its mapping
 * onto the shared signature model used for call validation.
 */
class IsPreprocessorMacroSignatureTest {

    /**
     * An empty or blank parameter list — `#define M() …` — declares no parameters at all, which must not
     * produce a phantom parameter with an empty name.
     */
    @Test
    fun `empty parameter list yields no parameters`() {
        assertTrue(parseMacroParameters("").isEmpty())
        assertTrue(parseMacroParameters("   ").isEmpty())
    }

    /**
     * The simplest ISPP form `#define M(A, B) …`: untyped, mandatory, by-value parameters keep their name and
     * carry neither a declared type nor a default.
     */
    @Test
    fun `untyped parameters keep their names`() {
        val parameters = parseMacroParameters("A, B")
        assertEquals(listOf("A", "B"), parameters.map { it.name })
        assertNull("An undeclared type must stay null so the body probing decides", parameters[0].declaredType)
        assertFalse(parameters[0].byRef)
        assertFalse(parameters[0].optional)
    }

    /**
     * ISPP allows a type specifier in front of the parameter name (`any`, `int`, `str`, `func`). `func` has no
     * counterpart in the expression type system and must therefore widen to `any` rather than produce a wrong
     * type error.
     */
    @Test
    fun `type specifiers are parsed`() {
        val parameters = parseMacroParameters("int A, str S, any X, func F")
        assertEquals(IsPreprocessorExprType.INT, parameters[0].declaredType)
        assertEquals(IsPreprocessorExprType.STR, parameters[1].declaredType)
        assertEquals(IsPreprocessorExprType.ANY, parameters[2].declaredType)
        assertEquals(IsPreprocessorExprType.ANY, parameters[3].declaredType)
    }

    /**
     * A `*` marks a by-reference parameter (`#define M(int *Result) …`), whose argument must be a macro name
     * ISPP can write back into. The marker must not become part of the parameter name.
     */
    @Test
    fun `by reference parameters are recognised`() {
        val parameters = parseMacroParameters("int *Result, str * Out")
        assertTrue(parameters[0].byRef)
        assertEquals("Result", parameters[0].name)
        assertTrue(parameters[1].byRef)
        assertEquals("Out", parameters[1].name)
    }

    /**
     * A trailing `= <expr>` makes the parameter optional (`#define Multiply(int A, int B = 10) A * B`); the
     * default expression is kept verbatim for the popup and for value evaluation.
     */
    @Test
    fun `default values make a parameter optional`() {
        val parameters = parseMacroParameters("int A, int B = 10")
        assertFalse(parameters[0].optional)
        assertTrue(parameters[1].optional)
        assertEquals("10", parameters[1].defaultValue)
    }

    /**
     * A default value may itself be a call or a string containing commas — splitting must happen at top-level
     * commas only, otherwise `Copy(S, 1, 2)` would be torn into three bogus parameters.
     */
    @Test
    fun `commas inside a default value do not split parameters`() {
        val parameters = parseMacroParameters("str S, str Part = Copy(S, 1, 2), str Sep = \",\"")
        assertEquals(listOf("S", "Part", "Sep"), parameters.map { it.name })
        assertEquals("Copy(S, 1, 2)", parameters[1].defaultValue)
        assertEquals("\",\"", parameters[2].defaultValue)
    }

    /**
     * A comparison inside a default value (`= A == 1`) must not be mistaken for the `=` that introduces the
     * default, which would cut the expression in half.
     */
    @Test
    fun `comparison inside a default value is not treated as the default separator`() {
        val parameters = parseMacroParameters("int A, int B = A == 1")
        assertEquals("A == 1", parameters[1].defaultValue)
    }

    /**
     * The offset of every parameter name points at the name inside the raw list, so a reference in the body can
     * be anchored on the declaration for navigation and rename.
     */
    @Test
    fun `parameter offsets point at the name`() {
        val raw = "int A, str *S = \"\""
        val parameters = parseMacroParameters(raw)
        assertEquals("A", raw.substring(parameters[0].offset, parameters[0].offset + 1))
        assertEquals("S", raw.substring(parameters[1].offset, parameters[1].offset + 1))
    }

    /**
     * The rendered declaration is what the parameter info popup and Quick Doc show, so it has to read like the
     * ISPP source it came from.
     */
    @Test
    fun `presentation renders the declaration`() {
        val parameters = parseMacroParameters("int A, str *S, B = 10")
        assertEquals("int A", parameters[0].presentation)
        assertEquals("str *S", parameters[1].presentation)
        assertEquals("B = 10", parameters[2].presentation)
    }

    /**
     * The signature model drives the call validation: parameters with a default are optional, so the accepted
     * argument count spans from the mandatory count to the declared count, and the declared types map onto the
     * parameter kinds.
     */
    @Test
    fun `signature maps declaration onto the shared model`() {
        val signature = macroSignatureOf("M", parseMacroParameters("int A, *C, str B = \"\""))
        assertEquals("Only the parameter with a default is optional", 2, signature.requiredCount)
        assertEquals(3, signature.maxCount)
        assertEquals(IsPreprocessorBuiltinParameterKind.INT, signature.parameters[0].kind)
        assertTrue(signature.parameters[1].byRef)
        assertEquals(IsPreprocessorBuiltinParameterKind.STR, signature.parameters[2].kind)
        assertTrue(signature.parameters[2].optional)
    }

    /**
     * A parameter without a declared type takes the type probed from the macro body, so the probing result
     * still reaches the call validation.
     */
    @Test
    fun `probed types fill in undeclared parameters`() {
        val signature = macroSignatureOf(
            "M",
            parseMacroParameters("int A, B"),
            probedTypes = listOf(IsPreprocessorExprType.ANY, IsPreprocessorExprType.STR),
        )
        assertEquals(IsPreprocessorBuiltinParameterKind.INT, signature.parameters[0].kind)
        assertEquals(IsPreprocessorBuiltinParameterKind.STR, signature.parameters[1].kind)
    }
}
