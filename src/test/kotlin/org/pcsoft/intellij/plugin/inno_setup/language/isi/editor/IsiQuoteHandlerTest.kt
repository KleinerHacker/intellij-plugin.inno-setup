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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for [IsiQuoteHandler] — auto-closing and skip-over behaviour of `"`.
 * Co-located with the handler (which lives in the `editor` package).
 */
class IsiQuoteHandlerTest : BasePlatformTestCase() {

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
        val result = IsiQuoteHandler().beforeCharTyped(
            '"', project, myFixture.editor, file, file.fileType
        )
        assertEquals(TypedHandlerDelegate.Result.CONTINUE, result)
    }
}
