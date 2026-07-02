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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.lang.annotation.HighlightSeverity
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase
import java.io.File
import kotlin.io.path.createTempDirectory

/**
 * Tests for the default `file`/`directory` attribute kind validation in [IsSectionAnnotator]
 * ([IsSectionAnnotator.annotatePathValue]). Absolute paths to real temporary files/directories are
 * used so resolution does not depend on the in-memory test filesystem of the script under test.
 */
class IsSectionPathAnnotatorTest : IsTimedBasePlatformTestCase() {

    private lateinit var tempFile: File
    private lateinit var tempDir: File

    override fun setUp() {
        super.setUp()
        tempFile = File.createTempFile("is-path-test", ".txt").apply { deleteOnExit() }
        tempDir = createTempDirectory("is-path-test").toFile().apply { deleteOnExit() }
    }

    private fun fwd(file: File) = file.absolutePath.replace('\\', '/')

    private fun highlights(content: String) =
        myFixture.run { configureByText(IsScriptFileType.INSTANCE, content); doHighlighting() }

    /** True when any annotation's description contains [needle] at the given [severity]. */
    private fun has(content: String, severity: HighlightSeverity, needle: String) =
        highlights(content).any {
            it.severity == severity && it.description?.contains(needle, ignoreCase = true) == true
        }

    // ── file kind ─────────────────────────────────────────────────────────────

    fun testExistingFileProducesNoError() {
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{app}\"\n"
        assertFalse("An existing file must not be flagged", has(text, HighlightSeverity.ERROR, "not found"))
    }

    fun testMissingFileProducesError() {
        val missing = fwd(File(tempDir, "does-not-exist.txt"))
        val text = "[Files]\nSource: \"$missing\"; DestDir: \"{app}\"\n"
        assertTrue("A missing file must be flagged", has(text, HighlightSeverity.ERROR, "File not found"))
    }

    fun testDirectoryWhereFileExpectedProducesError() {
        val text = "[Files]\nSource: \"${fwd(tempDir)}\"; DestDir: \"{app}\"\n"
        assertTrue(
            "A directory passed to a file attribute must be flagged",
            has(text, HighlightSeverity.ERROR, "Expected a file but found a directory")
        )
    }

    fun testWildcardIsSkipped() {
        val pattern = "${fwd(tempDir)}/*.txt"
        val text = "[Files]\nSource: \"$pattern\"; DestDir: \"{app}\"\n"
        assertFalse("Wildcard patterns must not be checked", has(text, HighlightSeverity.ERROR, "not found"))
    }

    fun testUnresolvableConstantIsSkipped() {
        // {app} is an install-time constant and cannot be resolved at compile time → no check.
        val text = "[Files]\nSource: \"{app}/missing.txt\"; DestDir: \"{app}\"\n"
        assertFalse("Unresolvable constants must not be checked", has(text, HighlightSeverity.ERROR, "not found"))
    }

    fun testCommaSeparatedListIsSkipped() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\nWizardImageFile=a.bmp,b.bmp\n"
        assertFalse("Comma-separated lists must not be checked", has(text, HighlightSeverity.ERROR, "not found"))
    }

    // ── directory kind ──────────────────────────────────────────────────────────

    fun testExistingDirectoryProducesNoError() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\nSourceDir=\"${fwd(tempDir)}\"\n"
        assertFalse("An existing directory must not be flagged", has(text, HighlightSeverity.ERROR, "not found"))
    }

    fun testMissingDirectoryProducesError() {
        val missing = fwd(File(tempDir, "nope"))
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\nSourceDir=\"$missing\"\n"
        assertTrue("A missing directory must be flagged", has(text, HighlightSeverity.ERROR, "Directory not found"))
    }

    fun testFileWhereDirectoryExpectedProducesError() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\nSourceDir=\"${fwd(tempFile)}\"\n"
        assertTrue(
            "A file passed to a directory attribute must be flagged",
            has(text, HighlightSeverity.ERROR, "Expected a directory but found a file")
        )
    }

    // ── optional existence (target/runtime paths) — invalid-character check only ──────

    fun testOptionalPathInvalidCharacterProducesError() {
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{app}\\bad|name\"\n"
        assertTrue(
            "An invalid character in an optional path must be flagged",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testOptionalPathWithConstantsProducesNoError() {
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{app}\\subdir\"\n"
        assertFalse(
            "A clean optional path with constants must not be flagged",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testOptionalPathExistenceIsNotChecked() {
        // DestDir is a target path (existence optional) → a non-existent directory must NOT be flagged.
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{app}\\does\\not\\exist\"\n"
        assertFalse(
            "Optional paths must not be checked for existence",
            has(text, HighlightSeverity.ERROR, "not found")
        )
    }

    fun testOptionalPathPipeInsideCodeConstantProducesNoError() {
        // '|' inside a {code:…} constant is legal and must be stripped before the character check.
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{code:GetDir|'x'}\"\n"
        assertFalse(
            "Characters inside {…} constants must not trigger the invalid-character check",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testOptionalPathMisplacedColonProducesError() {
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"{app}\\foo:bar\"\n"
        assertTrue(
            "A colon that is not a drive specifier must be flagged",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testOptionalPathDriveColonProducesNoError() {
        val text = "[Files]\nSource: \"${fwd(tempFile)}\"; DestDir: \"D:\\stuff\"\n"
        assertFalse(
            "A drive-letter colon must be allowed",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testOptionalPathUrlSchemeColonProducesNoError() {
        // [Icons] Filename may legitimately be a URL.
        val text = "[Icons]\nName: \"{group}\\X\"; Filename: \"https://example.com\"\n"
        assertFalse(
            "A URL scheme colon must be allowed",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    fun testDeleteWildcardNameProducesNoError() {
        val text = "[InstallDelete]\nType: files; Name: \"{app}\\*.log\"\n"
        assertFalse(
            "Wildcards in a delete path must not be treated as invalid characters",
            has(text, HighlightSeverity.ERROR, "Invalid character")
        )
    }

    // ── Languages.MessagesFile keeps its dedicated handling (the documented exception) ──

    fun testMessagesFileIsNotHandledByGenericFileCheck() {
        // compiler:Default.isl is resolved by the dedicated MessagesFile handler, not the generic
        // file check; with no installation path configured it yields a WARNING, never a generic
        // "File not found" ERROR.
        val text = "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n"
        assertFalse(
            "MessagesFile must not be validated by the generic file check",
            has(text, HighlightSeverity.ERROR, "File not found")
        )
    }
}
