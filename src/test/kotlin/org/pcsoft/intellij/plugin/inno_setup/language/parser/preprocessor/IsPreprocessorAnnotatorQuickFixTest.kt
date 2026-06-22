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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.RemoveIncludeQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.RemoveUnusedDefineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix.ReplaceIncludeWithLineQuickFix

/**
 * Tests for [RemoveUnusedDefineQuickFix].
 *
 * Testing via [com.intellij.testFramework.fixtures.CodeInsightTestFixture.launchAction] or
 * `myFixture.availableIntentions` is not viable here: after the quickfix deletes a
 * [org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine] from the host
 * document, IntelliJ's injection-cache cleanup invalidates the test [com.intellij.testFramework.LightVirtualFile],
 * making any subsequent access to `myFixture.file` throw.
 *
 * Instead we invoke the quickfix directly via the `internal` String constructor (which bypasses
 * injection entirely) inside a [WriteCommandAction], and read the document text synchronously
 * before the fixture teardown can observe the stale VF.
 */
class IsPreprocessorAnnotatorQuickFixTest : BasePlatformTestCase() {

    // ── Fix: Remove unused #define ────────────────────────────────────────────

    fun testRemoveUnusedDefineDeletesTheLine() {
        // Position the caret on [Setup] (outside any ISPP injection) so that
        // myFixture.editor is the ISS host editor, not an EditorWindow for the injection.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"value\"\n[<caret>Setup]\nAppName=MyApp\nAppVersion=1.0\n"
        )
        val issFile = myFixture.file
        val doc = myFixture.editor.document   // ISS host document (caret is outside injection)

        var result = ""
        WriteCommandAction.runWriteCommandAction(myFixture.project) {
            RemoveUnusedDefineQuickFix("UnusedConst").invoke(myFixture.project, myFixture.editor, issFile)
            result = doc.text
        }

        assertEquals("[Setup]\nAppName=MyApp\nAppVersion=1.0\n", result)
    }

    fun testRemoveUnusedDefinePreservesUsedDefines() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"x\"\n#define UsedConst \"1.0\"\n[<caret>Setup]\nAppName=MyApp\nAppVersion=1.0\n"
        )
        val issFile = myFixture.file
        val doc = myFixture.editor.document

        var result = ""
        WriteCommandAction.runWriteCommandAction(myFixture.project) {
            RemoveUnusedDefineQuickFix("UnusedConst").invoke(myFixture.project, myFixture.editor, issFile)
            result = doc.text
        }

        assertFalse("UnusedConst line must be removed", result.contains("UnusedConst"))
        assertTrue("UsedConst definition must be preserved", result.contains("#define UsedConst"))
    }

    // ── Fix: Remove #include (empty target) ───────────────────────────────────

    fun testRemoveIncludeDeletesTheLine() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#include \"part.iss\"\n[<caret>Setup]\nAppName=MyApp\nAppVersion=1.0\n"
        )
        val issFile = myFixture.file
        val doc = myFixture.editor.document

        var result = ""
        WriteCommandAction.runWriteCommandAction(myFixture.project) {
            RemoveIncludeQuickFix("part.iss").invoke(myFixture.project, myFixture.editor, issFile)
            result = doc.text
        }

        assertEquals("[Setup]\nAppName=MyApp\nAppVersion=1.0\n", result)
    }

    // ── Fix: Replace #include with its single line ────────────────────────────

    fun testReplaceIncludeWithSingleLineReplacesTheLine() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#include \"part.iss\"\n[<caret>Setup]\nAppName=MyApp\nAppVersion=1.0\n"
        )
        val issFile = myFixture.file
        val doc = myFixture.editor.document

        var result = ""
        WriteCommandAction.runWriteCommandAction(myFixture.project) {
            ReplaceIncludeWithLineQuickFix("part.iss", "AppPublisher=ACME")
                .invoke(myFixture.project, myFixture.editor, issFile)
            result = doc.text
        }

        assertEquals("AppPublisher=ACME\n[Setup]\nAppName=MyApp\nAppVersion=1.0\n", result)
    }
}
