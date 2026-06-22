/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.openapi.ui.TestInputDialog
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [IsExtractIncludeIntentionAction], which moves the selected full lines into a new file and
 * replaces them with an `#include` of that file.
 */
class IsExtractIncludeIntentionActionTest : BasePlatformTestCase() {

    private val action = IsExtractIncludeIntentionAction()

    override fun tearDown() {
        try {
            TestDialogManager.setTestInputDialog(TestInputDialog.DEFAULT)
            TestDialogManager.setTestDialog(com.intellij.openapi.ui.TestDialog.DEFAULT)
        } finally {
            super.tearDown()
        }
    }

    private fun runExtract() = action.invoke(project, myFixture.editor, myFixture.file)

    private fun answerFileName(name: String) =
        TestDialogManager.setTestInputDialog(object : TestInputDialog {
            override fun show(message: String): String = name
        })

    // ── isAvailable ───────────────────────────────────────────────────────────

    fun testNotAvailableWithoutSelection() {
        myFixture.configureByText("main.iss", "[Files]\nSource: \"a.exe\"\n")
        assertFalse(action.isAvailable(project, myFixture.editor, myFixture.file))
    }

    fun testAvailableWithSelection() {
        myFixture.configureByText("main.iss", "[Files]\n<selection>Source: \"a.exe\"\n</selection>")
        assertTrue(action.isAvailable(project, myFixture.editor, myFixture.file))
    }

    // ── invoke ────────────────────────────────────────────────────────────────

    fun testExtractCreatesFileAndReplacesWithInclude() {
        answerFileName("extracted.iss")
        myFixture.configureByText(
            "main.iss",
            "[Setup]\nAppName=Test\n<selection>[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n</selection>",
        )
        runExtract()

        myFixture.checkResult("[Setup]\nAppName=Test\n#include \"extracted.iss\"\n")

        val dir = (myFixture.file.virtualFile).parent
        val created = dir.findChild("extracted.iss")
        assertNotNull("Expected the extracted file to be created", created)
        assertEquals(
            "[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n",
            VfsUtilCore.loadText(created!!),
        )
    }

    fun testExtractExpandsPartialSelectionToFullLines() {
        answerFileName("part.iss")
        // Selection starts/ends mid-line; the whole lines must be extracted.
        myFixture.configureByText(
            "main.iss",
            "[Files]\nSour<selection>ce: \"a.exe\"</selection>\n",
        )
        runExtract()
        myFixture.checkResult("[Files]\n#include \"part.iss\"\n")

        val created = myFixture.file.virtualFile.parent.findChild("part.iss")!!
        assertEquals("Source: \"a.exe\"\n", VfsUtilCore.loadText(created))
    }

    fun testExtractOverwritesExistingFileWhenConfirmed() {
        answerFileName("inc.iss")
        TestDialogManager.setTestDialog { Messages.YES }
        myFixture.addFileToProject("inc.iss", "old content\n")
        myFixture.configureByText("main.iss", "[Setup]\n<selection>AppName=Test\n</selection>")
        runExtract()

        myFixture.checkResult("[Setup]\n#include \"inc.iss\"\n")
        val created = myFixture.file.virtualFile.parent.findChild("inc.iss")!!
        assertEquals("AppName=Test\n", VfsUtilCore.loadText(created))
    }

    fun testExtractAbortsWhenOverwriteDeclined() {
        answerFileName("inc.iss")
        TestDialogManager.setTestDialog { Messages.NO }
        myFixture.addFileToProject("inc.iss", "old content\n")
        myFixture.configureByText("main.iss", "[Setup]\n<selection>AppName=Test\n</selection>")
        runExtract()

        // Nothing changed in the script and the existing file is untouched.
        myFixture.checkResult("[Setup]\nAppName=Test\n")
        val existing = myFixture.file.virtualFile.parent.findChild("inc.iss")!!
        assertEquals("old content\n", VfsUtilCore.loadText(existing))
    }
}
