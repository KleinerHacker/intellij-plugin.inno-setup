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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection

/**
 * Tests for the structure-view tree built by [IssStructureViewElement] /
 * [IssStructureViewModel] / [IsiStructureViewFactory].
 */
class IsiStructureViewTest : BasePlatformTestCase() {

    private val script =
        "[Setup]\nAppName=My App\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"

    private fun issFile(content: String = script): IssFile {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        return myFixture.file as IssFile
    }

    fun testRootPresentationUsesFileNameAndIcon() {
        val file = issFile()
        val root = IssStructureViewElement(file)
        assertEquals(file.name, root.presentation.presentableText)
        assertNotNull("Root must have an icon", root.presentation.getIcon(false))
    }

    fun testFileChildrenAreSections() {
        val file = issFile()
        val children = IssStructureViewElement(file).children
        val names = children.map { it.presentation.presentableText }
        assertEquals(listOf("Setup", "Files"), names)
        children.forEach { assertTrue(it is IssStructureViewElement && it.value is IsiSection) }
    }

    fun testParameterSectionExposesEntryChildren() {
        val file = issFile()
        val filesSection = IssStructureViewElement(file).children
            .first { (it as IssStructureViewElement).value.let { v -> v is IsiSection && v.nameTextEquals("Files") } }
        val entries = (filesSection as IssStructureViewElement).children
        assertEquals("Files section must expose its single parameter entry", 1, entries.size)
        assertTrue((entries[0] as IssStructureViewElement).value is IsiParameterEntry)
        assertTrue(
            "Entry display name should reference the source",
            entries[0].presentation.presentableText!!.contains("app.exe")
        )
    }

    fun testDirectiveSectionHasTwoChildren() {
        val file = issFile()
        val setupSection = IssStructureViewElement(file).children
            .first { (it as IssStructureViewElement).value.let { v -> v is IsiSection && v.nameTextEquals("Setup") } }
        assertEquals(2, (setupSection as IssStructureViewElement).children.size)
    }

    fun testModelLeafClassification() {
        val file = issFile()
        val model = IssStructureViewModel(file)

        val entry = IssStructureViewElement(file).children
            .first { (it as IssStructureViewElement).value.let { v -> v is IsiSection && v.nameTextEquals("Files") } }
            .let { (it as IssStructureViewElement).children[0] as IssStructureViewElement }
        val section = IssStructureViewElement(file).children[0] as IssStructureViewElement

        assertTrue("Parameter entries are always leaves", model.isAlwaysLeaf(entry))
        assertFalse("Sections are not always leaves", model.isAlwaysLeaf(section))
    }

    fun testFactoryReturnsNullForNonIssFile() {
        val txt = myFixture.configureByText("notes.txt", "hello")
        assertNull(IsiStructureViewFactory().getStructureViewBuilder(txt))
    }

    /**
     * The structure-aware navigation bar resolves the current member via
     * [IssStructureViewModel.getCurrentEditorElement], which only works when the editor is
     * forwarded into the model. With a caret inside a parameter the model must report that
     * parameter entry — otherwise the navbar stays at file level.
     */
    fun testCurrentEditorElementResolvesParameterAtCaret() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[Setup]\nAppName=My App\n\n[Files]\nSource: \"app.<caret>exe\"; DestDir: \"{app}\"\n"
        )
        val file = myFixture.file as IssFile
        val model = IssStructureViewModel(file, myFixture.editor)
        assertTrue(
            "Caret in [Files] entry must resolve to that parameter entry",
            model.currentEditorElement is IsiParameterEntry
        )
    }

    fun testCurrentEditorElementIsNullWithoutEditor() {
        val file = issFile()
        // Regression guard: the no-editor model (used outside an editor) cannot report a member.
        assertNull(IssStructureViewModel(file).currentEditorElement)
    }

    private fun IsiSection.nameTextEquals(name: String): Boolean =
        nameText.equals(name, ignoreCase = true)
}
