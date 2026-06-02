package org.pcsoft.intellij.plugin.inno_setup.language.highlight

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IssQuoteHandlerTest : BasePlatformTestCase() {

    // --- Integration tests: auto-close on typing ---

    fun testDoubleQuoteAutoClosesInValue() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=<caret>")
        myFixture.type("\"")
        myFixture.checkResult("AppName=\"<caret>\"")
    }

    fun testTypingClosingQuoteSkipsOverExisting() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=\"<caret>\"")
        myFixture.type("\"")
        myFixture.checkResult("AppName=\"\"<caret>")
    }

    fun testTypingInsideQuotesDoesNotAddMore() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName=\"<caret>\"")
        myFixture.type("hello")
        myFixture.checkResult("AppName=\"hello<caret>\"")
    }
}
