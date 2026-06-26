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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.ide.ui.UISettings
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import com.intellij.util.CommonProcessors
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections

/**
 * Tests for [IsStructureAwareNavbar] — the navigation-bar model extension that reuses the
 * structure view. Verifies presentation, parent resolution and child enumeration.
 */
class IsStructureAwareNavbarTest : IsTimedBasePlatformTestCase() {

    private val navbar = IsStructureAwareNavbar()

    private val script =
        "[Setup]\nAppName=My App\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"

    private fun issFile(): IsScriptFile {
        myFixture.configureByText(IsScriptFileType.INSTANCE, script)
        return myFixture.file as IsScriptFile
    }

    private fun section(name: String): IsSectionBlock =
        issFile().sections.first { it.nameText.equals(name, ignoreCase = true) }

    fun testPresentableTextForSection() {
        assertEquals("Setup", navbar.getPresentableText(section("Setup")))
    }

    fun testPresentableTextForFile() {
        assertEquals(issFile().name, navbar.getPresentableText(issFile()))
    }

    fun testIconForSection() {
        assertNotNull(navbar.getIcon(section("Setup")))
    }

    fun testParentOfSectionIsFile() {
        val setup = section("Setup")
        assertEquals(setup.containingFile, navbar.getParent(setup))
    }

    fun testParentOfParameterEntryIsSection() {
        val files = section("Files")
        val entry = files.parameterEntryList.first()
        assertEquals(files, navbar.getParent(entry))
    }

    fun testProcessChildrenOfFileYieldsSections() = withMembersInNavbar(true) {
        val file = issFile()
        val collector = CommonProcessors.CollectProcessor<Any>()
        navbar.processChildren(file, file, collector)
        val sectionNames = collector.results
            .filterIsInstance<IsSectionBlock>()
            .map { it.nameText }
        assertEquals(listOf("Setup", "Files"), sectionNames)
    }

    fun testProcessChildrenOfSectionYieldsEntries() = withMembersInNavbar(true) {
        val files = section("Files")
        val collector = CommonProcessors.CollectProcessor<Any>()
        navbar.processChildren(files, files, collector)
        val entries = collector.results.filterIsInstance<IsSectionParameterEntry>()
        assertEquals(1, entries.size)
    }

    /**
     * Root cause of "the navbar does not show sections": the platform only expands structure
     * members when *Settings | Editor | General | Navigation Bar | Show members* is enabled.
     * With the setting off (the IDE default) [StructureAwareNavBarModelExtension.processChildren]
     * contributes nothing, so the navbar stays at file level — independent of our extension.
     */
    fun testNoMembersWhenShowMembersDisabled() = withMembersInNavbar(false) {
        val file = issFile()
        val collector = CommonProcessors.CollectProcessor<Any>()
        navbar.processChildren(file, file, collector)
        assertTrue(
            "Without 'Show members in navigation bar' no section children are contributed",
            collector.results.none { it is IsSectionBlock }
        )
    }

    private fun withMembersInNavbar(enabled: Boolean, block: () -> Unit) {
        val settings = UISettings.getInstance()
        val previous = settings.showMembersInNavigationBar
        settings.showMembersInNavigationBar = enabled
        try {
            block()
        } finally {
            settings.showMembersInNavigationBar = previous
        }
    }
}
