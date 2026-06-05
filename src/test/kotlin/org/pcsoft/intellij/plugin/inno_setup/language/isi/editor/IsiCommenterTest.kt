package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiCommenterTest : BasePlatformTestCase() {

    fun testLineCommentPrefix() {
        assertEquals(";", IsiCommenter().lineCommentPrefix)
    }

    fun testBlockCommentPrefixIsNull() {
        assertNull(IsiCommenter().blockCommentPrefix)
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
