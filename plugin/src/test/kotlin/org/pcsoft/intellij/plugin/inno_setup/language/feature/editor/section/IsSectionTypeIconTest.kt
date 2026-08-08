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

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.pcsoft.intellij.plugin.inno_setup.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import javax.swing.Icon

/**
 * Tests the icon that characterises a section by its entry syntax: the mapping in
 * [IsIcons.forSectionType] and its use in the section-name completion. The same mapping feeds
 * [IsSectionTypeInlayHintsProvider], which renders the icon behind the `[` of a section header.
 */
class IsSectionTypeIconTest : IsTimedBasePlatformTestCase() {

    /**
     * A directive section (`Key=Value`) is characterised by the `=` entry icon.
     */
    fun testDirectiveSectionMapsToDirectiveIcon() {
        assertSame(IsIcons.DirectiveEntry, IsIcons.forSectionType(IsSectionType.DIRECTIVE))
    }

    /**
     * A parameter section (`Key: Value`) is characterised by the `:` entry icon.
     */
    fun testParameterSectionMapsToParameterIcon() {
        assertSame(IsIcons.ParameterEntry, IsIcons.forSectionType(IsSectionType.PARAMETER))
    }

    /**
     * \[Code] holds free-form Pascal rather than entries, so it is characterised by the script icon.
     */
    fun testCodeSectionMapsToScriptIcon() {
        assertSame(IsIcons.ScriptFile, IsIcons.forSectionType(IsSectionType.CODE))
    }

    /**
     * A section the specification does not describe has no known entry syntax and therefore no icon —
     * this is what suppresses the inlay hint on an unknown section header.
     */
    fun testUnknownSectionMapsToNoIcon() {
        assertNull(IsIcons.forSectionType(null))
    }

    /**
     * The section-name completion carries the entry-syntax icon on every offered section, so the syntax
     * is visible before the first entry of the section exists.
     */
    fun testSectionCompletionCarriesEntrySyntaxIcon() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[<caret>\n")
        val items = myFixture.completeBasic() ?: emptyArray()

        assertSame("[Setup] must be offered with the '=' icon", IsIcons.DirectiveEntry, iconOf(items, "Setup"))
        assertSame("[Files] must be offered with the ':' icon", IsIcons.ParameterEntry, iconOf(items, "Files"))
        assertSame("[Code] must be offered with the script icon", IsIcons.ScriptFile, iconOf(items, "Code"))
    }

    /**
     * The inlay hint is placed directly behind the `[` of a section header, so the icon appears between the
     * bracket and the section name.
     */
    fun testSectionHeaderCarriesInlayBehindOpeningBracket() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAppName=Test\n\n[Files]\n")
        myFixture.doHighlighting()
        val text = myFixture.file.text
        val offsets = myFixture.editor.inlayModel
            .getInlineElementsInRange(0, myFixture.editor.document.textLength)
            .map { it.offset }

        assertTrue("Inlay must sit behind the '[' of [Setup]", text.indexOf("Setup]") in offsets)
        assertTrue("Inlay must sit behind the '[' of [Files]", text.indexOf("Files]") in offsets)
    }

    /**
     * A section the specification does not know carries no inlay — there is no entry syntax to announce.
     */
    fun testUnknownSectionHeaderCarriesNoInlay() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[NoSuchSection]\n")
        myFixture.doHighlighting()
        val offsets = myFixture.editor.inlayModel
            .getInlineElementsInRange(0, myFixture.editor.document.textLength)
            .map { it.offset }

        assertTrue("An unknown section must not carry a type inlay, offsets were: $offsets", offsets.isEmpty())
    }

    /** The icon the rendered lookup element for [name] carries. */
    private fun iconOf(items: Array<LookupElement>, name: String): Icon? {
        val element = items.firstOrNull { it.lookupString == name }
        assertNotNull("Expected a '$name' lookup element, offered: ${items.map { it.lookupString }}", element)

        return LookupElementPresentation().also { element!!.renderElement(it) }.icon
    }
}
