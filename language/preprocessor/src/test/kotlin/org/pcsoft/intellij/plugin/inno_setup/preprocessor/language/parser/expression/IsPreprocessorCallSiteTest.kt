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
import org.junit.Test

/**
 * Verifies [findEnclosingCall], which locates the call surrounding the caret for the parameter info popup.
 * The caret position is marked with `|` in the test input.
 */
class IsPreprocessorCallSiteTest {

    /** Runs [findEnclosingCall] on [markedText], using the position of `|` as the caret offset. */
    private fun callAt(markedText: String): IsPreprocessorCallSite? {
        val offset = markedText.indexOf('|')
        require(offset >= 0) { "test input must contain a caret marker '|'" }
        return findEnclosingCall(markedText.replace("|", ""), offset)
    }

    /** Inside the argument list of a simple call, that call and its first argument are reported. */
    @Test
    fun `caret in the first argument reports index zero`() {
        val call = callAt("#define X Copy(|\"abc\", 1, 2)")
        assertNotNull(call)
        assertEquals("Copy", call!!.name)
        assertEquals(0, call.argumentIndex)
    }

    /** Each top-level comma advances the argument index. */
    @Test
    fun `commas advance the argument index`() {
        assertEquals(1, callAt("#define X Copy(\"abc\", |1, 2)")!!.argumentIndex)
        assertEquals(2, callAt("#define X Copy(\"abc\", 1, |2)")!!.argumentIndex)
    }

    /** A comma inside a string literal must not be counted as an argument separator. */
    @Test
    fun `comma inside a string literal is ignored`() {
        val call = callAt("#define X Copy(\"a,b,c\", |1, 2)")
        assertEquals(1, call!!.argumentIndex)
    }

    /** A parenthesis inside a string literal must not open a call. */
    @Test
    fun `parenthesis inside a string literal is ignored`() {
        val call = callAt("#define X Copy(\"a(b\", |1, 2)")
        assertEquals("Copy", call!!.name)
        assertEquals(1, call.argumentIndex)
    }

    /** In a nested call the innermost one wins, with its own argument index. */
    @Test
    fun `nested call reports the innermost callee`() {
        val call = callAt("#define X Copy(Trim(| \"a\"), 1, 2)")
        assertEquals("Trim", call!!.name)
        assertEquals(0, call.argumentIndex)
    }

    /** After a nested call has been closed, the outer call is reported again. */
    @Test
    fun `after a closed nested call the outer call is reported`() {
        val call = callAt("#define X Copy(Trim(\"a\"), |1, 2)")
        assertEquals("Copy", call!!.name)
        assertEquals(1, call.argumentIndex)
    }

    /** Grouping parentheses are transparent: the enclosing named call stays the reported one. */
    @Test
    fun `grouping parentheses do not hide the call`() {
        val call = callAt("#define X Power((2 + |3), 4)")
        assertEquals("Power", call!!.name)
        assertEquals(0, call.argumentIndex)
    }

    /** Whitespace between the callee name and its argument list is allowed. */
    @Test
    fun `whitespace between name and argument list is allowed`() {
        assertEquals("Copy", callAt("#define X Copy (|\"a\", 1, 2)")!!.name)
    }

    /** Outside any argument list there is no call to describe. */
    @Test
    fun `caret outside a call yields null`() {
        assertNull(callAt("#define X 1 + |2"))
        assertNull(callAt("#define X Copy(\"a\", 1, 2)|"))
    }

    /** A parenthesis that follows a number is grouping, not a call. */
    @Test
    fun `number before a parenthesis is not a callee`() {
        assertNull(callAt("#define X 2(|3)"))
    }

    /** The reported offsets point at the callee name and its argument list. */
    @Test
    fun `offsets point at the name and the argument list`() {
        val text = "#define X Copy(\"a\", 1, 2)"
        val call = findEnclosingCall(text, text.indexOf("\"a\""))!!
        assertEquals(text.indexOf("Copy"), call.nameStart)
        assertEquals(text.indexOf('('), call.argumentListStart)
    }
}
