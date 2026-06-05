package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

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
