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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

class IsCommenterTest : BasePlatformTestCase() {

    fun testLineCommentPrefix() {
        assertEquals(";", IsCommenter().lineCommentPrefix)
    }

    fun testBlockCommentPrefixIsNull() {
        assertNull(IsCommenter().blockCommentPrefix)
    }

    fun testCommentLine() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "AppName<caret>=My App\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult(";AppName=My App\n")
    }

    fun testUncommentLine() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, ";AppName<caret>=My App\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult("AppName=My App\n")
    }

    fun testCommentEmptyLine() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "<caret>\n")
        myFixture.performEditorAction("CommentByLineComment")
        myFixture.checkResult(";\n")
    }
}
