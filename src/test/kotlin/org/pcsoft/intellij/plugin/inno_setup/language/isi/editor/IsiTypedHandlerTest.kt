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

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate.Result
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for [IsiTypedHandler.checkAutoPopup] — the branches that decide whether
 * an auto-popup is scheduled. The handler is invoked directly so the assertion
 * is on the returned [Result], independent of the asynchronous popup machinery.
 */
class IsiTypedHandlerTest : BasePlatformTestCase() {

    private val handler = IsiTypedHandler()

    /** Configures ISS content and returns the top-level [IssFile] (recovers from injection). */
    private fun issFile(content: String): PsiFile {
        val file = myFixture.configureByText(IssFileType.INSTANCE, content)
        return if (file is IssFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file)
    }

    private fun checkAutoPopup(c: Char, content: String): Result {
        val file = issFile(content)
        return handler.checkAutoPopup(c, project, myFixture.editor, file)
    }

    fun testOpenBracketSchedulesPopup() {
        assertEquals(Result.STOP, checkAutoPopup('[', "<caret>"))
    }

    fun testOpenBraceSchedulesPopup() {
        assertEquals(Result.STOP, checkAutoPopup('{', "[Setup]\nAppName=<caret>\n"))
    }

    fun testHashAfterBraceSchedulesPopup() {
        assertEquals(Result.STOP, checkAutoPopup('#', "[Setup]\nAppName={<caret>\n"))
    }

    fun testHashWithoutPrecedingBraceContinues() {
        assertEquals(Result.CONTINUE, checkAutoPopup('#', "[Setup]\nAppName=<caret>\n"))
    }

    fun testUnrelatedCharContinues() {
        assertEquals(Result.CONTINUE, checkAutoPopup('A', "[Setup]\n<caret>\n"))
    }

    fun testNonIssFileContinues() {
        val file = myFixture.configureByText("notes.txt", "<caret>")
        assertEquals(Result.CONTINUE, handler.checkAutoPopup('[', project, myFixture.editor, file))
    }
}
