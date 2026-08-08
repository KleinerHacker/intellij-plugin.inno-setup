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

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Integration tests for [IsSectionLanguageInlayHintsProvider]: drives the provider over real PSI and
 * inspects the editor's inlay model after a highlighting pass.
 *
 * The negative tests assert that no inlay sits at the *value* in question rather than that the document
 * carries no inlay at all: [IsSectionTypeInlayHintsProvider] contributes an inlay to every section header,
 * so an empty inlay model would no longer be a statement about the language flags.
 */
class IsSectionLanguageInlayHintsTest : IsTimedBasePlatformTestCase() {

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

    /**
     * A `LanguageID` holding a known LCID is preceded by the flag of that locale.
     */
    fun testLanguageIdKnownLcidShowsInlay() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$0409\n"
        val offsets = inlineInlayOffsets(text)
        val expected = myFixture.file.text.indexOf("\$0409")
        assertTrue("A flag inlay must be placed before a known LanguageID value", expected in offsets)
    }

    /**
     * A recognised LCID with a curated entry (`$0436`, Afrikaans) is treated like any other known locale.
     */
    fun testLanguageIdCuratedLcidShowsInlay() {
        // $0436 (Afrikaans) is a recognised LCID and now has a curated entry → inlay shown.
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$0436\n"
        val offsets = inlineInlayOffsets(text)
        val expected = myFixture.file.text.indexOf("\$0436")
        assertTrue("A flag inlay must be placed before a recognised LanguageID value", expected in offsets)
    }

    /**
     * An unassigned LCID maps to no locale, so no flag is shown at the value.
     */
    fun testLanguageIdInvalidValueShowsNoInlay() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$9999\n"
        val offsets = inlineInlayOffsets(text)
        val value = myFixture.file.text.indexOf("\$9999")
        assertFalse("No inlay for an unassigned LanguageID", value in offsets)
    }

    /**
     * The flag belongs to `MessagesFile` (whose `.isl` declares the LanguageID), not to the user-chosen
     * `Name` of the \[Languages] entry.
     */
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

    /**
     * A `MessagesFile` that cannot be resolved yields no LanguageID and therefore no flag.
     */
    fun testLanguagesUnknownMessagesFileShowsNoInlay() {
        val text = VALID_SETUP + "\n[Languages]\nName: \"klingon\"; MessagesFile: \"custom.isl\"\n"
        val offsets = inlineInlayOffsets(text)
        val fileText = myFixture.file.text
        assertFalse("No inlay for a non-resolvable MessagesFile", fileText.indexOf("\"custom.isl\"") in offsets)
        assertFalse("No inlay for the Name of an unresolved entry", fileText.indexOf("\"klingon\"") in offsets)
    }

    /**
     * A `lang.` prefixed message key carries the flag of the language the prefix refers to.
     */
    fun testMessagesLanguagePrefixShowsFlagInlay() {
        val text = VALID_SETUP +
                "\n[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        val offsets = inlineInlayOffsets(text)
        val keyOffset = myFixture.file.text.indexOf("english.WelcomeLabel1")
        assertTrue("Flag inlay before the lang.-prefixed message key", keyOffset in offsets)
    }
}
