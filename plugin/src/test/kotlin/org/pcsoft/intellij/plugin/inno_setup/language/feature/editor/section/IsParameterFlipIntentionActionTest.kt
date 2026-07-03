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

import com.intellij.openapi.command.WriteCommandAction
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsParameterFlipIntentionAction], which swaps the parameter pair before the caret with the
 * following one inside a parameter section entry. The caret is expected to sit directly behind the
 * `;` separating the two pairs.
 */
class IsParameterFlipIntentionActionTest : IsTimedBasePlatformTestCase() {

    private val action = IsParameterFlipIntentionAction()

    private fun configure(content: String) =
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)

    private fun isAvailable() =
        action.isAvailable(project, myFixture.editor, myFixture.file)

    private fun runFlip() = WriteCommandAction.runWriteCommandAction(project) {
        action.invoke(project, myFixture.editor, myFixture.file)
    }

    // ── isAvailable ───────────────────────────────────────────────────────────

    fun testAvailableRightAfterSeparatorInParameterEntry() {
        configure("[Files]\nSource: \"a.exe\";<caret> DestDir: \"{app}\"\n")
        assertTrue("Intention must be offered behind a ';' inside a parameter entry", isAvailable())
    }

    fun testNotAvailableWhenCaretNotBehindSeparator() {
        configure("[Files]\nSource: \"a.exe\"; DestDir: <caret>\"{app}\"\n")
        assertFalse("Intention must not be offered when the caret is not behind a ';'", isAvailable())
    }

    fun testNotAvailableInDirectiveSection() {
        // '[Setup]' directives use 'Key=Value' and contain no parameter entries.
        configure("[Setup]\nAppName=Test;<caret>\nAppVersion=1.0\n")
        assertFalse("Intention must not be offered outside a parameter entry", isAvailable())
    }

    // ── invoke ────────────────────────────────────────────────────────────────

    fun testFlipSwapsTheTwoParameterPairs() {
        configure("[Files]\nSource: \"a.exe\";<caret> DestDir: \"{app}\"\n")
        runFlip()
        myFixture.checkResult("[Files]\nDestDir: \"{app}\"; Source: \"a.exe\"\n")
    }

    fun testFlipOnlySwapsTheAdjacentPair() {
        // Source <-> DestDir are swapped; the trailing Flags pair stays in place.
        configure("[Files]\nSource: \"a.exe\";<caret> DestDir: \"{app}\"; Flags: isreadme\n")
        runFlip()
        myFixture.checkResult("[Files]\nDestDir: \"{app}\"; Source: \"a.exe\"; Flags: isreadme\n")
    }
}
