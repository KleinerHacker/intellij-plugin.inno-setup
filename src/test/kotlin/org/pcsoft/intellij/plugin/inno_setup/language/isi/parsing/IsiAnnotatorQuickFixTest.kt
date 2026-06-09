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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiAnnotatorQuickFixTest : BasePlatformTestCase() {

    private fun configure(content: String) {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        myFixture.doHighlighting()
    }

    private fun findIntention(prefix: String): IntentionAction =
        myFixture.availableIntentions.firstOrNull { it.text.startsWith(prefix) }
            ?: error("No intention with prefix '$prefix' found. Available: ${myFixture.availableIntentions.map { it.text }}")

    // ── Fix 1: Add missing sections (file-level) ──────────────────────────────

    fun testAddMissingSectionWhenMissing() {
        configure("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n")
        myFixture.launchAction(findIntention("Add missing section"))
        val result = myFixture.file.text
        assertTrue("Expected [Setup] section to be added", result.contains("[Setup]\n"))
        assertTrue("Expected AppName directive", result.contains("AppName=MyApp"))
        assertTrue("Expected AppVersion directive", result.contains("AppVersion=1.0"))
    }

    fun testAddMissingSectionAppendsAtEndWhenNoCode() {
        val input = "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        configure(input)
        myFixture.launchAction(findIntention("Add missing section"))
        val result = myFixture.file.text
        val setupIdx = result.indexOf("[Setup]")
        val filesIdx = result.indexOf("[Files]")
        assertTrue("[Setup] must appear after [Files] when no [Code] is present", setupIdx > filesIdx)
    }

    fun testAddMissingSectionInsertsBeforeCode() {
        configure("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n[Code]\n")
        myFixture.launchAction(findIntention("Add missing section"))
        val result = myFixture.file.text
        val setupIdx = result.indexOf("[Setup]")
        val codeIdx = result.indexOf("[Code]")
        assertTrue("[Setup] must appear before [Code]", setupIdx < codeIdx)
        assertTrue("Expected AppName directive", result.contains("AppName=MyApp"))
        assertTrue("Expected AppVersion directive", result.contains("AppVersion=1.0"))
    }

    // ── Fix 2a: Add missing directives ───────────────────────────────────────

    fun testAddMissingDirectivesBothMissing() {
        configure("[Set<caret>up]\n")
        myFixture.launchAction(findIntention("Add missing directive"))
        assertEquals("[Setup]\nAppName=MyApp\nAppVersion=1.0\n", myFixture.file.text)
    }

    fun testAddMissingDirectivesOnlyVersionMissing() {
        configure("[Set<caret>up]\nAppName=Test\n")
        myFixture.launchAction(findIntention("Add missing directive"))
        assertEquals("[Setup]\nAppName=Test\nAppVersion=1.0\n", myFixture.file.text)
    }

    fun testAddMissingDirectivesAppendsAfterExistingEntry() {
        configure("[Set<caret>up]\nCompression=lzma2\n")
        myFixture.launchAction(findIntention("Add missing directive"))
        val result = myFixture.file.text
        val compressionIdx = result.indexOf("Compression=lzma2")
        val appNameIdx = result.indexOf("AppName=MyApp")
        val appVersionIdx = result.indexOf("AppVersion=1.0")
        assertTrue("AppName must appear after Compression", appNameIdx > compressionIdx)
        assertTrue("AppVersion must appear after Compression", appVersionIdx > compressionIdx)
    }

    // ── Fix 2b: Add missing parameters ───────────────────────────────────────

    fun testAddMissingParameterDestDir() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: <caret>\"app.exe\"\n"
        )
        myFixture.launchAction(findIntention("Add missing parameter"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: {app}\n",
            myFixture.file.text
        )
    }

    fun testAddMissingParametersBothRequired() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nFlags: <caret>ignoreversion\n"
        )
        myFixture.launchAction(findIntention("Add missing parameter"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nFlags: ignoreversion; Source: MyProg.exe; DestDir: {app}\n",
            myFixture.file.text
        )
    }

    fun testAddMissingParametersRegistryMissingSubkey() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Registry]\nRoot: <caret>HKLM\n"
        )
        myFixture.launchAction(findIntention("Add missing parameter"))
        val result = myFixture.file.text
        assertTrue("Expected Subkey parameter to be added", result.contains("; Subkey: Software\\MyCompany\\MyApp"))
    }

    // ── Fix 3: Move [Code] to last ────────────────────────────────────────────

    fun testMoveCodeSectionLast() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[<caret>Code]\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        )
        myFixture.launchAction(findIntention("Move [Code]"))
        val result = myFixture.file.text
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n\n[Code]\n",
            result
        )
    }

    fun testMoveCodeSectionLastMultipleSectionsAfter() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[<caret>Code]\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n\n" +
                    "[Registry]\nRoot: HKLM; Subkey: Software\\MyApp\n"
        )
        myFixture.launchAction(findIntention("Move [Code]"))
        val result = myFixture.file.text
        val codeIdx = result.indexOf("[Code]")
        val filesIdx = result.indexOf("[Files]")
        val registryIdx = result.indexOf("[Registry]")
        assertTrue("[Code] must be after [Files]", codeIdx > filesIdx)
        assertTrue("[Code] must be after [Registry]", codeIdx > registryIdx)
    }

    // ── Fix 4: Remove trailing semicolon ─────────────────────────────────────

    fun testRemoveTrailingSemicolon() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"<caret>;\n"
        )
        myFixture.launchAction(findIntention("Remove trailing semicolon"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n",
            myFixture.file.text
        )
    }

    fun testRemoveTrailingSemicolonPreservesInternalSemicolons() {
        // Caret at trailing ';'; the internal '; DestDir' separator must survive.
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"<caret>;\n"
        )
        myFixture.launchAction(findIntention("Remove trailing semicolon"))
        val result = myFixture.file.text
        assertTrue("Internal semicolons between parameters must be preserved", result.contains("; DestDir"))
        assertFalse("Trailing semicolon must be removed", result.trimEnd('\n').endsWith(";"))
    }

    // ── Fix 5: Remove empty section ───────────────────────────────────────────

    fun testRemoveEmptySection() {
        configure("[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[<caret>Registry]\n")
        myFixture.launchAction(findIntention("Remove empty section"))
        assertEquals("[Setup]\nAppName=MyApp\nAppVersion=1.0\n", myFixture.file.text)
    }

    fun testRemoveEmptySectionPreservesOtherSections() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[<caret>Registry]\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        )
        myFixture.launchAction(findIntention("Remove empty section"))
        val result = myFixture.file.text
        assertTrue("[Files] section must be preserved after removing [Registry]", result.contains("[Files]"))
        assertFalse("Empty [Registry] section must be removed", result.contains("[Registry]"))
    }

    // ── Fix 6: Remove redundant flag ──────────────────────────────────────────

    fun testRemoveRedundantFlag() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external nocompress<caret>ion\n"
        )
        myFixture.launchAction(findIntention("Remove redundant flag"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external\n",
            myFixture.file.text
        )
    }

    fun testRemoveRedundantFlagPreservesImplyingFlag() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external nocompress<caret>ion\n"
        )
        myFixture.launchAction(findIntention("Remove redundant flag"))
        val result = myFixture.file.text
        assertTrue("The implying flag 'external' must be preserved", result.contains("Flags: external"))
        assertFalse("The redundant flag 'nocompression' must be removed", result.contains("nocompression"))
    }

    // ── Fix 7: Add required flags ─────────────────────────────────────────────

    fun testAddRequiredFlags() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarc<caret>hive\n"
        )
        myFixture.launchAction(findIntention("Add required flag"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarchive external ignoreversion\n",
            myFixture.file.text
        )
    }

    fun testAddRequiredFlagsOnlyAddsMissing() {
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarc<caret>hive external\n"
        )
        myFixture.launchAction(findIntention("Add required flag"))
        assertEquals(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarchive external ignoreversion\n",
            myFixture.file.text
        )
    }

    fun testMoveCodeSectionLastPreservesCodeContent() {
        // Use ISS comment lines as [Code] content — Pascal code cannot be parsed as ISS entries
        // and would corrupt the section structure, making the annotation unreachable.
        configure(
            "[Setup]\nAppName=MyApp\nAppVersion=1.0\n\n" +
                    "[<caret>Code]\n" +
                    "; code section content\n\n" +
                    "[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        )
        myFixture.launchAction(findIntention("Move [Code]"))
        val result = myFixture.file.text
        val codeIdx = result.indexOf("[Code]")
        val filesIdx = result.indexOf("[Files]")
        assertTrue("[Code] must be after [Files]", codeIdx > filesIdx)
        assertTrue("Comment content must be preserved in moved section",
            result.contains("; code section content"))
        assertTrue("Comment must appear after [Code] header",
            result.indexOf("; code section content") > codeIdx)
    }
}
