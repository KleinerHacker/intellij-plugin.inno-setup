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
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewElement
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewFactory
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewModel
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParameterEntry

/**
 * Tests for the structure-view tree built by [org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewElement] /
 * [org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewModel] / [org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsStructureViewFactory].
 */
class IsSectionStructureViewTest : BasePlatformTestCase() {

    private val script =
        "[Setup]\nAppName=My App\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"

    private fun issFile(content: String = script): IsScriptFile {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.file as IsScriptFile
    }

    fun testRootPresentationUsesFileNameAndIcon() {
        val file = issFile()
        val root = IsStructureViewElement(file)
        assertEquals(file.name, root.presentation.presentableText)
        assertNotNull("Root must have an icon", root.presentation.getIcon(false))
    }

    fun testFileChildrenAreSections() {
        val file = issFile()
        val children = IsStructureViewElement(file).children
        val names = children.map { it.presentation.presentableText }
        assertEquals(listOf("Setup", "Files"), names)
        children.forEach { assertTrue(it is IsStructureViewElement && it.value is IsSectionBlock) }
    }

    fun testParameterSectionExposesEntryChildren() {
        val file = issFile()
        val filesSection = IsStructureViewElement(file).children
            .first { (it as IsStructureViewElement).value.let { v -> v is IsSectionBlock && v.nameTextEquals("Files") } }
        val entries = (filesSection as IsStructureViewElement).children
        assertEquals("Files section must expose its single parameter entry", 1, entries.size)
        assertTrue((entries[0] as IsStructureViewElement).value is IsSectionParameterEntry)
        assertTrue(
            "Entry display name should reference the source",
            entries[0].presentation.presentableText!!.contains("app.exe")
        )
    }

    fun testDirectiveSectionHasTwoChildren() {
        val file = issFile()
        val setupSection = IsStructureViewElement(file).children
            .first { (it as IsStructureViewElement).value.let { v -> v is IsSectionBlock && v.nameTextEquals("Setup") } }
        assertEquals(2, (setupSection as IsStructureViewElement).children.size)
    }

    fun testModelLeafClassification() {
        val file = issFile()
        val model = IsStructureViewModel(file)

        val entry = IsStructureViewElement(file).children
            .first { (it as IsStructureViewElement).value.let { v -> v is IsSectionBlock && v.nameTextEquals("Files") } }
            .let { (it as IsStructureViewElement).children[0] as IsStructureViewElement }
        val section = IsStructureViewElement(file).children[0] as IsStructureViewElement

        assertTrue("Parameter entries are always leaves", model.isAlwaysLeaf(entry))
        assertFalse("Sections are not always leaves", model.isAlwaysLeaf(section))
    }

    fun testFactoryReturnsNullForNonIssFile() {
        val txt = myFixture.configureByText("notes.txt", "hello")
        assertNull(IsStructureViewFactory().getStructureViewBuilder(txt))
    }

    /**
     * The structure-aware navigation bar resolves the current member via
     * [IsStructureViewModel.getCurrentEditorElement], which only works when the editor is
     * forwarded into the model. With a caret inside a parameter the model must report that
     * parameter entry — otherwise the navbar stays at file level.
     */
    fun testCurrentEditorElementResolvesParameterAtCaret() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Setup]\nAppName=My App\n\n[Files]\nSource: \"app.<caret>exe\"; DestDir: \"{app}\"\n"
        )
        val file = myFixture.file as IsScriptFile
        val model = IsStructureViewModel(file, myFixture.editor)
        assertTrue(
            "Caret in [Files] entry must resolve to that parameter entry",
            model.currentEditorElement is IsSectionParameterEntry
        )
    }

    fun testCurrentEditorElementIsNullWithoutEditor() {
        val file = issFile()
        // Regression guard: the no-editor model (used outside an editor) cannot report a member.
        assertNull(IsStructureViewModel(file).currentEditorElement)
    }

    private fun IsSectionBlock.nameTextEquals(name: String): Boolean =
        nameText.equals(name, ignoreCase = true)
}
