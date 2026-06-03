package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.editor.IssBraceMatcher
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssBraceMatcherTest : BasePlatformTestCase() {

    // --- Unit tests on getBracePairs() ---

    fun testBracePairsCount() {
        assertEquals(3, IssBraceMatcher.PAIRS.size)
    }

    fun testSquareBracketPair() {
        val pair = IssBraceMatcher.PAIRS.first { it.leftBraceType == IssTypes.LBRACKET }
        assertEquals(IssTypes.RBRACKET, pair.rightBraceType)
        assertTrue("[] must be structural", pair.isStructural)
    }

    fun testCurlyBracePair() {
        val pair = IssBraceMatcher.PAIRS.first { it.leftBraceType == IssTypes.LBRACE }
        assertEquals(IssTypes.RBRACE, pair.rightBraceType)
        assertFalse("{} must not be structural", pair.isStructural)
    }

    fun testParenPair() {
        val pair = IssBraceMatcher.PAIRS.first { it.leftBraceType == IssTypes.LPAREN }
        assertEquals(IssTypes.RPAREN, pair.rightBraceType)
        assertFalse("() must not be structural", pair.isStructural)
    }

    fun testIsPairedBracesAllowedBeforeTypeAlwaysTrue() {
        val matcher = IssBraceMatcher()
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IssTypes.LBRACKET, null))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IssTypes.LBRACE, IssTypes.IDENTIFIER))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IssTypes.LPAREN, IssTypes.CRLF))
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
