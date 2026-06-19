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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for [IsIncludeInlineIntentionAction], which replaces an `#include "file"` line with the verbatim
 * content of the referenced file (one level only).
 */
class IsIncludeInlineIntentionActionTest : BasePlatformTestCase() {

    private val action = IsIncludeInlineIntentionAction()

    private fun isAvailable() = action.isAvailable(project, myFixture.editor, myFixture.file)

    private fun runInline() = WriteCommandAction.runWriteCommandAction(project) {
        action.invoke(project, myFixture.editor, myFixture.file)
    }

    // ── isAvailable ───────────────────────────────────────────────────────────

    fun testAvailableOnIncludeWithExistingFile() {
        myFixture.addFileToProject("inc.iss", "[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n")
        myFixture.configureByText("main.iss", "#inc<caret>lude \"inc.iss\"\n")
        assertTrue("Intention must be offered on an #include resolving to an existing file", isAvailable())
    }

    fun testNotAvailableWhenFileMissing() {
        myFixture.configureByText("main.iss", "#inc<caret>lude \"missing.iss\"\n")
        assertFalse("Intention must not be offered when the file does not exist", isAvailable())
    }

    fun testNotAvailableOutsideIncludeLine() {
        myFixture.addFileToProject("inc.iss", "[Files]\n")
        myFixture.configureByText("main.iss", "#include \"inc.iss\"\n[Set<caret>up]\nAppName=Test\n")
        assertFalse("Intention must not be offered outside an #include line", isAvailable())
    }

    // ── invoke ────────────────────────────────────────────────────────────────

    fun testInlineReplacesIncludeWithFileContent() {
        myFixture.addFileToProject("inc.iss", "[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"")
        myFixture.configureByText("main.iss", "#inc<caret>lude \"inc.iss\"\n[Setup]\nAppName=Test\n")
        runInline()
        myFixture.checkResult("[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n[Setup]\nAppName=Test\n")
    }

    fun testNestedIncludeStaysVerbatim() {
        myFixture.addFileToProject("inner.iss", "[Files]\n")
        myFixture.addFileToProject("outer.iss", "#include \"inner.iss\"")
        myFixture.configureByText("main.iss", "#inc<caret>lude \"outer.iss\"\n")
        runInline()
        myFixture.checkResult("#include \"inner.iss\"\n")
    }
}
