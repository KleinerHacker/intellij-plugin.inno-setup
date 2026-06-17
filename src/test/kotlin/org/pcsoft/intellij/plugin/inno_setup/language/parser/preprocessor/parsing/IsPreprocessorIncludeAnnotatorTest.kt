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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing

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
            errors("unknown directive").isNotEmpty()
        )
    }

    fun testErrorsOfIncludedFileAreReportedOnInclude() {
        // part.iss itself includes a missing file → that problem must surface on main's #include.
        myFixture.addFileToProject("part.iss", "#include \"deep-missing.iss\"\n")
        open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        assertTrue(
            "A problem inside the included file must be collected onto the #include",
            errors("deep-missing.iss").isNotEmpty()
        )
    }
}
