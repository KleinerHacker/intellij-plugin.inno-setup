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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.highlight

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.editor.IsiBraceMatcher
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiBraceMatcherTest : BasePlatformTestCase() {

    // --- Unit tests on getBracePairs() ---

    fun testBracePairsCount() {
        assertEquals(3, IsiBraceMatcher.PAIRS.size)
    }

    fun testSquareBracketPair() {
        val pair = IsiBraceMatcher.PAIRS.first { it.leftBraceType == IsiTypes.LBRACKET }
        assertEquals(IsiTypes.RBRACKET, pair.rightBraceType)
        assertTrue("[] must be structural", pair.isStructural)
    }

    fun testCurlyBracePair() {
        val pair = IsiBraceMatcher.PAIRS.first { it.leftBraceType == IsiTypes.LBRACE }
        assertEquals(IsiTypes.RBRACE, pair.rightBraceType)
        assertFalse("{} must not be structural", pair.isStructural)
    }

    fun testParenPair() {
        val pair = IsiBraceMatcher.PAIRS.first { it.leftBraceType == IsiTypes.LPAREN }
        assertEquals(IsiTypes.RPAREN, pair.rightBraceType)
        assertFalse("() must not be structural", pair.isStructural)
    }

    fun testIsPairedBracesAllowedBeforeTypeAlwaysTrue() {
        val matcher = IsiBraceMatcher()
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsiTypes.LBRACKET, null))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsiTypes.LBRACE, IsiTypes.IDENTIFIER))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsiTypes.LPAREN, IsiTypes.CRLF))
    }

    // --- Integration tests: auto-close on typing ---

    fun testSquareBracketAutoCloses() {
        myFixture.configureByText(IssFileType.INSTANCE, "<caret>")
        myFixture.type("[")
        myFixture.checkResult("[<caret>]")
    }

    fun testCurlyBraceAutoClosesInValue() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("{")
        myFixture.checkResult("AppName={<caret>}")
    }

    fun testParenAutoClosesInValue() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("(")
        myFixture.checkResult("AppName=(<caret>)")
    }

    fun testTypingClosingSquareBracketSkipsOverExisting() {
        myFixture.configureByText(IssFileType.INSTANCE, "[<caret>]")
        myFixture.type("]")
        myFixture.checkResult("[]<caret>")
    }

    fun testTypingClosingCurlyBraceSkipsOverExisting() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName={<caret>}")
        myFixture.type("}")
        myFixture.checkResult("AppName={}<caret>")
    }
}
