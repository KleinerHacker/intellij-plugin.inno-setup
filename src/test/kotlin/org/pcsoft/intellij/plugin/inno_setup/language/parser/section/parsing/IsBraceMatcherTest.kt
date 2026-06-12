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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsBraceMatcher
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

class IsBraceMatcherTest : BasePlatformTestCase() {

    // --- Unit tests on getBracePairs() ---

    fun testBracePairsCount() {
        assertEquals(3, IsBraceMatcher.PAIRS.size)
    }

    fun testSquareBracketPair() {
        val pair = IsBraceMatcher.PAIRS.first { it.leftBraceType == IsSectionTypes.LBRACKET }
        assertEquals(IsSectionTypes.RBRACKET, pair.rightBraceType)
        assertTrue("[] must be structural", pair.isStructural)
    }

    fun testCurlyBracePair() {
        val pair = IsBraceMatcher.PAIRS.first { it.leftBraceType == IsSectionTypes.LBRACE }
        assertEquals(IsSectionTypes.RBRACE, pair.rightBraceType)
        assertFalse("{} must not be structural", pair.isStructural)
    }

    fun testParenPair() {
        val pair = IsBraceMatcher.PAIRS.first { it.leftBraceType == IsSectionTypes.LPAREN }
        assertEquals(IsSectionTypes.RPAREN, pair.rightBraceType)
        assertFalse("() must not be structural", pair.isStructural)
    }

    fun testIsPairedBracesAllowedBeforeTypeAlwaysTrue() {
        val matcher = IsBraceMatcher()
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsSectionTypes.LBRACKET, null))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsSectionTypes.LBRACE, IsSectionTypes.IDENTIFIER))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsSectionTypes.LPAREN, IsSectionTypes.CRLF))
    }

    // --- Integration tests: auto-close on typing ---

    fun testSquareBracketAutoCloses() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "<caret>")
        myFixture.type("[")
        myFixture.checkResult("[<caret>]")
    }

    fun testCurlyBraceAutoClosesInValue() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("{")
        myFixture.checkResult("AppName={<caret>}")
    }

    fun testParenAutoClosesInValue() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("(")
        myFixture.checkResult("AppName=(<caret>)")
    }

    fun testTypingClosingSquareBracketSkipsOverExisting() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[<caret>]")
        myFixture.type("]")
        myFixture.checkResult("[]<caret>")
    }

    fun testTypingClosingCurlyBraceSkipsOverExisting() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "AppName={<caret>}")
        myFixture.type("}")
        myFixture.checkResult("AppName={}<caret>")
    }
}
