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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies [parseBuiltinSignature]: every shape of a `signature` string used by the bundled ISPP
 * specification must be turned into the structured parameter model the expression validation relies on.
 */
class IsPreprocessorBuiltinSignatureTest {

    /** A signature with a single typed parameter yields one required string parameter and a string result. */
    @Test
    fun `single typed parameter is parsed`() {
        val signature = parseBuiltinSignature("AddBackslash(S: str): str")
        assertNotNull(signature)
        assertEquals("AddBackslash", signature!!.name)
        assertEquals(IsPreprocessorExprType.STR, signature.returnType)
        assertEquals(1, signature.parameters.size)
        assertEquals("S", signature.parameters[0].name)
        assertEquals(IsPreprocessorBuiltinParameterKind.STR, signature.parameters[0].kind)
        assertEquals(false, signature.parameters[0].byRef)
        assertEquals(false, signature.parameters[0].optional)
        assertEquals(1, signature.requiredCount)
        assertEquals(1, signature.maxCount)
    }

    /** Several parameters of mixed types keep their declaration order and types. */
    @Test
    fun `multiple parameters keep order and types`() {
        val signature = parseBuiltinSignature("Copy(S: str, Index: int, Count: int): str")!!
        assertEquals(3, signature.parameters.size)
        assertEquals(IsPreprocessorBuiltinParameterKind.STR, signature.parameters[0].kind)
        assertEquals(IsPreprocessorBuiltinParameterKind.INT, signature.parameters[1].kind)
        assertEquals(IsPreprocessorBuiltinParameterKind.INT, signature.parameters[2].kind)
        assertEquals(3, signature.requiredCount)
    }

    /** Parameters with a default value are optional, which widens the accepted argument count. */
    @Test
    fun `default values make parameters optional`() {
        val signature = parseBuiltinSignature(
            """Exec(CmdLine: str, Params: str = "", WorkingDir: str = "", ShowCmd: int = 0, Wait: int = 0): int"""
        )!!
        assertEquals(5, signature.maxCount)
        assertEquals(1, signature.requiredCount)
        assertTrue(signature.parameters.drop(1).all { it.optional })
        assertEquals("1 to 5 arguments", signature.arityDescription)
    }

    /** A string default containing a comma must not split the parameter list. */
    @Test
    fun `comma inside a string default does not split parameters`() {
        val signature = parseBuiltinSignature("""F(A: str, B: str = "x,y"): str""")!!
        assertEquals(2, signature.parameters.size)
        assertEquals(1, signature.requiredCount)
    }

    /** A trailing `*` marks a by-reference parameter that the function writes back into. */
    @Test
    fun `by reference parameters are detected`() {
        val signature = parseBuiltinSignature("StringChange(S: str*, FromStr: str, ToStr: str): int")!!
        assertTrue(signature.parameters[0].byRef)
        assertEquals(IsPreprocessorBuiltinParameterKind.STR, signature.parameters[0].kind)
        assertEquals(false, signature.parameters[1].byRef)
    }

    /** `Ident` and `Array` denote un-evaluated symbol parameters (`Defined(X)`, `DimOf(A)`). */
    @Test
    fun `symbol parameters are detected`() {
        assertEquals(
            IsPreprocessorBuiltinParameterKind.IDENT,
            parseBuiltinSignature("Defined(Ident): int")!!.parameters[0].kind,
        )
        assertEquals(
            IsPreprocessorBuiltinParameterKind.ARRAY,
            parseBuiltinSignature("DimOf(Array): int")!!.parameters[0].kind,
        )
    }

    /** `any` parameters accept every value type. */
    @Test
    fun `any parameter kind is parsed`() {
        val signature = parseBuiltinSignature("Str(Value: any): str")!!
        assertEquals(IsPreprocessorBuiltinParameterKind.ANY, signature.parameters[0].kind)
    }

    /** A signature without a parameter list accepts no arguments at all. */
    @Test
    fun `empty parameter list yields no parameters`() {
        val signature = parseBuiltinSignature("IsWin64(): int")!!
        assertEquals(0, signature.maxCount)
        assertEquals("no arguments", signature.arityDescription)
    }

    /** A signature without a result type falls back to the specification return type (`void`). */
    @Test
    fun `missing result type falls back to the given return type`() {
        val signature = parseBuiltinSignature("Error(Message: str)", IsPreprocessorExprType.VOID)!!
        assertEquals(IsPreprocessorExprType.VOID, signature.returnType)
        assertEquals(1, signature.requiredCount)
    }

    /** A string that is not a call signature at all is rejected instead of producing a bogus model. */
    @Test
    fun `malformed signature is rejected`() {
        assertNull(parseBuiltinSignature("no parentheses here"))
        assertNull(parseBuiltinSignature("(S: str): str"))
    }
}
