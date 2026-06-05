package org.pcsoft.intellij.plugin.inno_setup.language.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for [IssQuoteHandler] — auto-closing and skip-over behaviour of `"`.
 * Co-located with the handler (which lives in the `editor` package).
 */
class IssQuoteHandlerTest : BasePlatformTestCase() {

    // ── Auto-close ────────────────────────────────────────────────────────────

    fun testDoubleQuoteAutoClosesInValue() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("\"")
        myFixture.checkResult("AppName=\"<caret>\"")
    }

    // ── Skip over an existing closing quote ───────────────────────────────────

    fun testTypingClosingQuoteSkipsOverExistingEmptyString() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=\"<caret>\"")
        myFixture.type("\"")
        myFixture.checkResult("AppName=\"\"<caret>")
    }

    fun testTypingClosingQuoteSkipsOverExistingAfterContent() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"app<caret>\"\n")
        myFixture.type("\"")
        myFixture.checkResult("[Files]\nSource: \"app\"<caret>\n")
    }

    fun testTypingClosingQuoteSkipsOverExistingAfterConstant() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"{app}<caret>\"\n")
        myFixture.type("\"")
        myFixture.checkResult("[Files]\nSource: \"{app}\"<caret>\n")
    }

    // ── No auto-close while inside a string literal ───────────────────────────

    fun testNoAutoCloseInsideUnterminatedStringAfterContent() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"app<caret>\n")
        myFixture.type("\"")
        myFixture.checkResult("[Files]\nSource: \"app\"<caret>\n")
    }

    fun testTypingTextInsideQuotesDoesNotAddMore() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=\"<caret>\"")
        myFixture.type("hello")
        myFixture.checkResult("AppName=\"hello<caret>\"")
    }

    // ── QUOTE token directly before the caret: opening vs. closing ────────────

    // Caret sits right after an *opening* quote (token before the quote is not a
    // string token) → no auto-close, a single quote is inserted after the caret.
    fun testNoAutoCloseRightAfterOpeningQuote() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"<caret>\n")
        myFixture.type("\"")
        myFixture.checkResult("[Files]\nSource: \"\"<caret>\n")
    }

    // Caret sits right after a *closing* quote of a complete string (token before
    // the quote is STRING_PART) → typing starts a new auto-closed string.
    fun testAutoCloseAfterCompleteString() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"app\"<caret>\n")
        myFixture.type("\"")
        myFixture.checkResult("[Files]\nSource: \"app\"\"<caret>\"\n")
    }

    // ── Non-ISS files are left to the default handler ─────────────────────────

    fun testNonIssFileReturnsContinue() {
        val file = myFixture.configureByText("notes.txt", "value <caret>")
        val result = IssQuoteHandler().beforeCharTyped(
            '"', project, myFixture.editor, file, file.fileType
        )
        assertEquals(TypedHandlerDelegate.Result.CONTINUE, result)
    }
}
