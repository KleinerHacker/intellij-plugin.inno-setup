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

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Integration tests for [IsSectionLanguageInlayHintsProvider]: drives the provider over real PSI and
 * inspects the editor's inlay model after a highlighting pass.
 */
class IsSectionLanguageInlayHintsTest : BasePlatformTestCase() {

    private val VALID_SETUP = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    /** Offsets of all inline inlays contributed for the given file content. */
    private fun inlineInlayOffsets(content: String): List<Int> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        myFixture.doHighlighting()
        val doc = myFixture.editor.document
        return myFixture.editor.inlayModel
            .getInlineElementsInRange(0, doc.textLength)
            .map { it.offset }
    }

    fun testLanguageIdKnownLcidShowsInlay() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$0409\n"
        val offsets = inlineInlayOffsets(text)
        val expected = myFixture.file.text.indexOf("\$0409")
        assertTrue("A flag inlay must be placed before a known LanguageID value", expected in offsets)
    }

    fun testLanguageIdCuratedLcidShowsInlay() {
        // $0436 (Afrikaans) is a recognised LCID and now has a curated entry → inlay shown.
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$0436\n"
        val offsets = inlineInlayOffsets(text)
        val expected = myFixture.file.text.indexOf("\$0436")
        assertTrue("A flag inlay must be placed before a recognised LanguageID value", expected in offsets)
    }

    fun testLanguageIdInvalidValueShowsNoInlay() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$9999\n"
        assertTrue("No inlay for an unassigned LanguageID", inlineInlayOffsets(text).isEmpty())
    }

    fun testLanguagesMessagesFileShowsFlagInlayButNameDoesNot() {
        val text = VALID_SETUP + "\n[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n"
        val offsets = inlineInlayOffsets(text)
        val fileText = myFixture.file.text
        assertFalse("Name no longer carries a flag inlay", fileText.indexOf("\"english\"") in offsets)
        assertTrue(
            "Flag inlay before MessagesFile string (from its LanguageID)",
            fileText.indexOf("\"compiler:Default.isl\"") in offsets
        )
    }

    fun testLanguagesUnknownMessagesFileShowsNoInlay() {
        val text = VALID_SETUP + "\n[Languages]\nName: \"klingon\"; MessagesFile: \"custom.isl\"\n"
        assertTrue("No inlay for a non-resolvable MessagesFile", inlineInlayOffsets(text).isEmpty())
    }

    fun testMessagesLanguagePrefixShowsFlagInlay() {
        val text = VALID_SETUP +
                "\n[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        val offsets = inlineInlayOffsets(text)
        val keyOffset = myFixture.file.text.indexOf("english.WelcomeLabel1")
        assertTrue("Flag inlay before the lang.-prefixed message key", keyOffset in offsets)
    }
}
