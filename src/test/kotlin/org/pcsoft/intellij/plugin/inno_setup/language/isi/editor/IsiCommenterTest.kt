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
