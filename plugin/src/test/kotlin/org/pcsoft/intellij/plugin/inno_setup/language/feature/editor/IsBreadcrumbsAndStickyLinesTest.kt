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

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.components.breadcrumbs.StickyLineInfo
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
 * the preprocessor blocks must appear as sticky lines (branch-aware), but never as a breadcrumb.
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

    private fun stickyOffsets(offset: Int): List<Int> =
        collector.computeStickyLineInfos(hostVirtualFile(), hostDocument(), offset)
            .map(StickyLineInfo::textOffset)

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

    fun testCollectorHandlesIssFiles() {
        assertTrue(collector.handlesFile(hostVirtualFile()))
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

    // --- sticky lines: branch aware -----------------------------------------------------------------------

    fun testIfBranchSticksWithIfLine() {
        val offsets = stickyOffsets(offsetOf("debug.exe"))

        assertTrue("The [Files] section must stick", offsets.contains(section("Files").textRange.startOffset))
        assertTrue("The #if line must stick", offsets.contains(offsetOf("#if")))
        assertFalse("The #elif line must not stick here", offsets.contains(offsetOf("#elif")))
        assertFalse("The #else line must not stick here", offsets.contains(offsetOf("#else")))
    }

    fun testElifBranchReplacesIfLine() {
        val offsets = stickyOffsets(offsetOf("beta.exe"))

        assertTrue("The [Files] section must stick", offsets.contains(section("Files").textRange.startOffset))
        assertTrue("The #elif line must stick", offsets.contains(offsetOf("#elif")))
        assertFalse("The #elif replaces the #if line", offsets.contains(offsetOf("#if")))
        assertFalse("The #else line must not stick here", offsets.contains(offsetOf("#else")))
    }

    fun testElseBranchSticksBelowItsHeader() {
        val offsets = stickyOffsets(offsetOf("app.exe"))

        assertTrue("The #else line must stick", offsets.contains(offsetOf("#else")))
        assertTrue("The preceding #elif header must stick as well", offsets.contains(offsetOf("#elif")))
        assertFalse("The #if line is replaced by the #elif header", offsets.contains(offsetOf("#if")))
    }

    fun testStickyLinesOutsidePreprocessorBlockHaveNoBlock() {
        val offsets = stickyOffsets(offsetOf("AppName"))

        assertFalse("No #if block encloses the [Setup] entries", offsets.contains(offsetOf("#if")))
        assertTrue("The [Setup] section must stick", offsets.contains(section("Setup").textRange.startOffset))
    }

    fun testSubroutineBlockIsSticky() {
        val offsets = stickyOffsets(offsetOf("#endsub"))

        assertTrue("The enclosing #sub line must stick", offsets.contains(offsetOf("#sub")))
    }
}
