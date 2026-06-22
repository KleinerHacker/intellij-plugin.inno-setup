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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for the `#include` validation in [IsPreprocessorAnnotator]: the value must be a string and a literal
 * path must point to an existing file (its content is not parser-checked).
 */
class IsPreprocessorIncludeAnnotatorTest : BasePlatformTestCase() {

    private fun open(name: String, content: String): PsiFile {
        val file = myFixture.addFileToProject(name, content)
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        return file
    }

    private fun errors(needle: String) =
        myFixture.doHighlighting().filter {
            it.severity.name == "ERROR" && it.description?.contains(needle, ignoreCase = true) == true
        }

    private fun warnings(needle: String) =
        myFixture.doHighlighting().filter {
            it.severity.name == "WARNING" && it.description?.contains(needle, ignoreCase = true) == true
        }

    private fun weakWarnings(needle: String) =
        myFixture.doHighlighting().filter {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains(needle, ignoreCase = true) == true
        }

    fun testMissingIncludeFileProducesError() {
        open("main.iss", "#include \"missing.iss\"\n")
        assertTrue("A non-existent include must be flagged", errors("Included file not found").isNotEmpty())
    }

    fun testExistingIncludeFileProducesNoError() {
        myFixture.addFileToProject("part.iss", "[Files]\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("An existing include must not be flagged", errors("Included file not found").isEmpty())
    }

    fun testNonStringIncludeProducesError() {
        open("main.iss", "#include 1 + 2\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("A numeric #include value must be flagged", errors("#include requires a string value").isNotEmpty())
    }

    fun testEmptyIncludeStringProducesError() {
        open("main.iss", "#include \"\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("An empty #include string must be flagged", errors("must not be empty").isNotEmpty())
    }

    fun testUnknownFlagInIncludedFileIsReportedOnInclude() {
        myFixture.addFileToProject("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"; Flags: bogusflag\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "An unknown flag inside the included file must be collected onto the #include",
            errors("unknown flag").isNotEmpty()
        )
    }

    fun testUnknownDirectiveInIncludedFileIsReportedOnInclude() {
        myFixture.addFileToProject("part.iss", "[Setup]\nNotARealDirective=1\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "An unknown directive inside the included file must be collected onto the #include",
            errors("NotARealDirective").isNotEmpty()
        )
    }

    fun testErrorsOfIncludedFileAreReportedOnInclude() {
        // part.iss itself includes a missing file → in the effective script that #include stays verbatim and
        // its 'not found' problem must surface on main's #include (the topmost directive that pulled it in).
        myFixture.addFileToProject("part.iss", "#include \"deep-missing.iss\"\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A problem inside the included file must be collected onto the #include",
            errors("deep-missing.iss").isNotEmpty()
        )
    }

    fun testIncludedConstantResolvedAgainstMainDefineProducesNoError() {
        // {#MyVar} is unknown in part.iss alone, but the combined effective script defines it in main → valid.
        myFixture.addFileToProject("part.iss", "[Setup]\nAppPublisher={#MyVar}\n")
        open("main.iss", "#define MyVar \"ACME\"\n#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A constant resolved via a main-file #define must not be flagged on the #include",
            errors("Unknown constant").isEmpty()
        )
    }

    fun testUndefinedConstantInIncludedFileIsReportedOnInclude() {
        // {#Missing} is defined nowhere → unknown in the combined script → surfaces on the #include.
        myFixture.addFileToProject("part.iss", "[Setup]\nAppPublisher={#Missing}\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "An unknown constant introduced by the include must be collected onto the #include",
            errors("Unknown constant").isNotEmpty()
        )
    }

    fun testFragmentMissingRequiredSectionIsNotReportedOnInclude() {
        // part.iss is a bare [Files] fragment (no [Setup]); valid only as part of main. The file-level
        // 'required section missing' problem must not be attributed to the #include.
        myFixture.addFileToProject("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A fragment's missing mandatory section must not be flagged on the #include",
            errors("Required section").isEmpty()
        )
    }

    fun testEmptyIncludedFileProducesWeakWarning() {
        myFixture.addFileToProject("part.iss", "")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("An empty included file must produce a weak warning", weakWarnings("empty file").isNotEmpty())
    }

    fun testSingleLineIncludedFileProducesWeakWarning() {
        myFixture.addFileToProject("part.iss", "AppName=x")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("A single-line included file must produce a weak warning", weakWarnings("single-line").isNotEmpty())
    }

    fun testSingleLineWithTrailingNewlineStillCountsAsSingle() {
        myFixture.addFileToProject("part.iss", "AppName=x\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A trailing newline must not count as a second line",
            weakWarnings("single-line").isNotEmpty()
        )
    }

    fun testMultiLineIncludedFileProducesNoWeakWarning() {
        myFixture.addFileToProject("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue("A multi-line included file must not be flagged as empty", weakWarnings("empty file").isEmpty())
        assertTrue("A multi-line included file must not be flagged as single-line", weakWarnings("single-line").isEmpty())
    }

    fun testWarningFromIncludedLineIsReportedOnInclude() {
        // An out-of-range LanguageID is a WARNING; coming from the included line it must surface on the #include.
        myFixture.addFileToProject("part.iss", "[LangOptions]\nLanguageID=99999\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A warning introduced by the include must be collected onto the #include",
            warnings("language identifier").isNotEmpty()
        )
    }
}
