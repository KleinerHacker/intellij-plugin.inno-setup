package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class IssCommenterTest : BasePlatformTestCase() {

    fun testLineCommentPrefix() {
        assertEquals(";", IssCommenter().lineCommentPrefix)
    }

    fun testBlockCommentPrefixIsNull() {
        assertNull(IssCommenter().blockCommentPrefix)
    }

    fun testCommentLine() {
        myFixture.configureByText(IssFileType.INSTANCE, "AppName<caret>=My App\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult(";AppName=My App\n")
    }

    fun testUncommentLine() {
        myFixture.configureByText(IssFileType.INSTANCE, ";AppName<caret>=My App\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult("AppName=My App\n")
    }

    fun testCommentEmptyLine() {
        myFixture.configureByText(IssFileType.INSTANCE, "<caret>\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult(";\n")
    }
}
