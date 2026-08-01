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

import com.intellij.codeInsight.breadcrumbs.FileBreadcrumbsCollector
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.breadcrumbs.StickyLineInfo
import com.intellij.xml.breadcrumbs.PsiFileBreadcrumbsCollector
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.sections
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsBreadcrumbsProvider] and [IsFileBreadcrumbsCollector] — the breadcrumbs bar and the editor's
 * sticky lines are driven by the same infrastructure, so both are asserted on **one and the same** script:
 * sections are breadcrumb *and* sticky, entries are breadcrumb only, preprocessor lines are neither.
 */
class IsBreadcrumbsAndStickyLinesTest : IsTimedBasePlatformTestCase() {

    private val provider = IsBreadcrumbsProvider()

    private val script = "#define Debug\n" +
            "[Setup]\n" +
            "AppName=My App\n" +
            "\n" +
            "[Files]\n" +
            "#if Debug\n" +
            "Source: \"debug.exe\"; DestDir: \"{app}\"\n" +
            "#elif Beta\n" +
            "Source: \"beta.exe\"; DestDir: \"{app}\"\n" +
            "#else\n" +
            "Source: \"app.exe\"; DestDir: \"{app}\"\n" +
            "#endif\n" +
            "#sub Helper\n" +
            "#endsub\n"

    private lateinit var collector: IsFileBreadcrumbsCollector

    override fun setUp() {
        super.setUp()
        collector = IsFileBreadcrumbsCollector(project)
        myFixture.configureByText(IsScriptFileType.INSTANCE, script)
    }

    /**
     * The host script — `myFixture.file` is the *injected* ISPP fragment whenever the caret sits on a
     * preprocessor line (here: the leading `#define`).
     */
    private fun issFile(): IsScriptFile =
        InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file) as IsScriptFile

    private fun section(name: String): IsSectionBlock =
        issFile().sections.first { it.nameText.equals(name, ignoreCase = true) }

    /** The first `Source:` entry — a *sibling* of `[Files]`, because the `#if` line ends the section block. */
    private fun sourceEntry(): IsSectionParameterEntry =
        PsiTreeUtil.findChildrenOfType(issFile(), IsSectionParameterEntry::class.java).first()

    private fun offsetOf(text: String): Int = issFile().text.indexOf(text)

    private fun hostVirtualFile() = issFile().virtualFile

    private fun hostDocument() = myFixture.getDocument(issFile())

    private fun crumbTexts(offset: Int): List<String> =
        collector.computeCrumbs(hostVirtualFile(), hostDocument(), offset, true)
            .map { it.text }

    // --- provider -----------------------------------------------------------------------------------------

    fun testProviderLanguage() {
        assertEquals(listOf("ISS"), provider.languages.map { it.id })
    }

    fun testSectionIsBreadcrumbAndSticky() {
        val setup = section("Setup")
        assertTrue(provider.acceptElement(setup))
        assertTrue(provider.acceptStickyElement(setup))
        assertEquals("Setup", provider.getElementInfo(setup))
        assertNotNull(provider.getElementIcon(setup))
    }

    fun testParameterEntryIsBreadcrumbButNotSticky() {
        val entry = sourceEntry()
        assertTrue(provider.acceptElement(entry))
        assertFalse("Single-line entries are useless as sticky lines", provider.acceptStickyElement(entry))
    }

    fun testEntryBelowPreprocessorLineResolvesItsSectionByOffset() {
        assertEquals(section("Files"), provider.getParent(sourceEntry()))
    }

    /**
     * The plugin's collector must **not** claim a plain script file: only the platform's own collector turns
     * [IsBreadcrumbsProvider.acceptStickyElement] into sticky lines, and it is registered `order="last"`, so
     * claiming the file here would silence them.
     */
    fun testCollectorLeavesPlainScriptFilesToThePlatform() {
        assertFalse(collector.handlesFile(hostVirtualFile()))
        assertEquals(
            PsiFileBreadcrumbsCollector::class.java,
            FileBreadcrumbsCollector.findBreadcrumbsCollector(project, hostVirtualFile()).javaClass
        )
    }

    // --- breadcrumbs --------------------------------------------------------------------------------------

    fun testBreadcrumbsShowSectionWithoutPreprocessor() {
        val texts = crumbTexts(offsetOf("debug.exe"))

        assertTrue("Breadcrumbs must show the section: $texts", texts.any { it == "Files" })
        assertTrue("Breadcrumbs must not show the preprocessor block: $texts", texts.none { it.contains("#") })
    }

    fun testNoBreadcrumbOnPreprocessorLine() {
        listOf("#define", "#if", "#elif", "#else", "#sub").forEach { directive ->
            val texts = crumbTexts(offsetOf(directive) + 1)
            assertTrue(
                "A preprocessor line ($directive) must not produce an injected-fragment crumb: $texts",
                texts.none { it.contains("Injected", ignoreCase = true) || it.contains("ISPP") }
            )
        }
    }

    // --- sticky lines -------------------------------------------------------------------------------------

    /**
     * End-to-end: the section header must actually reach the editor as a sticky line. This asserts the whole
     * chain — the platform picks its own collector, that collector asks [IsBreadcrumbsProvider], and
     * `acceptStickyElement` reports the section.
     *
     * `StickyLineInfo` is `@ApiStatus.Internal`; using it is fine **here** because test code is not part of
     * the published plugin, and this is the only way to assert the feature end to end.
     */
    @Suppress("UnstableApiUsage")
    fun testSectionReachesTheEditorAsStickyLine() {
        val offset = offsetOf("debug.exe")
        val offsets = FileBreadcrumbsCollector.findBreadcrumbsCollector(project, hostVirtualFile())
            .computeStickyLineInfos(hostVirtualFile(), hostDocument(), offset)
            .map(StickyLineInfo::textOffset)

        assertTrue(
            "The [Files] section must stick: $offsets",
            offsets.contains(section("Files").textRange.startOffset)
        )
    }

    /**
     * Sticky lines are PSI-based only. A preprocessor line is *not* an [IsSectionBlock] (it ends the section
     * block in the grammar), so nothing on it can be sticky — the plain-range API that would allow it is
     * `@ApiStatus.Internal`.
     */
    fun testPreprocessorLinesAreNotStickyElements() {
        listOf("#if", "#elif", "#else", "#sub").forEach { directive ->
            val element = issFile().findElementAt(offsetOf(directive) + 1)!!
            assertFalse(
                "A preprocessor line ($directive) has no sticky PSI element",
                generateSequence(element) { it.parent }.takeWhile { it !is IsScriptFile }
                    .any { provider.acceptStickyElement(it) }
            )
        }
    }
}
