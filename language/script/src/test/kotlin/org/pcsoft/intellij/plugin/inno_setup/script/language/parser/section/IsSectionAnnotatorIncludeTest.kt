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

import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests that file-level mandatory checks run on the effective, `#include`-resolved script: required sections
 * and directives may be spread across the main file and its includes, and fragments are not flagged on their
 * own.
 */
class IsSectionAnnotatorIncludeTest : IsTimedBasePlatformTestCase() {

    private fun open(file: PsiFile) = myFixture.run {
        configureFromExistingVirtualFile(file.virtualFile); doHighlighting()
    }

    private fun hasError(needle: String) = myFixture.doHighlighting().any {
        it.severity.name == "ERROR" && it.description?.contains(needle, ignoreCase = true) == true
    }

    fun testRequiredDirectivesSatisfiedByIncludeProduceNoError() {
        myFixture.addFileToProject("setup.iss", "AppName=Test\nAppVersion=1.0\n")
        val main = myFixture.addFileToProject("main.iss", "[Setup]\n#include \"setup.iss\"\n")
        open(main)
        assertFalse(
            "Required [Setup] directives provided via #include must not be flagged",
            hasError("Required directive")
        )
    }

    fun testRequiredDirectivesMissingDespiteIncludeProduceError() {
        myFixture.addFileToProject("empty.iss", "; nothing useful\n")
        val main = myFixture.addFileToProject("main.iss", "[Setup]\n#include \"empty.iss\"\n")
        open(main)
        assertTrue(
            "Required [Setup] directives still missing after include must be flagged",
            hasError("Required directive")
        )
    }

    fun testIncludedFragmentIsNotFlaggedForMissingSection() {
        // frag.iss is included by main.iss → it must not be flagged on its own for the missing [Setup].
        myFixture.addFileToProject("main.iss", "#include \"frag.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")
        val frag = myFixture.addFileToProject("frag.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        open(frag)
        assertFalse(
            "An included fragment must not be flagged for a missing required section",
            hasError("Required section")
        )
    }

    fun testStandaloneScriptStillFlaggedForMissingSection() {
        val solo = myFixture.addFileToProject("solo.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        open(solo)
        assertTrue(
            "A standalone script missing [Setup] must still be flagged",
            hasError("Required section")
        )
    }

    // ── declarations resolved over the effective (#include-resolved) script ────

    fun testCustomMessageDefinedInIncludeIsNotFlagged() {
        myFixture.addFileToProject("messages.iss", "[CustomMessages]\nMyMsg=Hello\n")
        val main = myFixture.addFileToProject(
            "main.iss",
            "[Setup]\nAppName={cm:MyMsg}\nAppVersion=1.0\n#include \"messages.iss\"\n",
        )
        open(main)
        assertFalse(
            "A {cm:…} message provided via #include must not be flagged as unknown",
            hasError("Unknown custom message")
        )
    }

    fun testUnknownCustomMessageStillFlaggedWithInclude() {
        myFixture.addFileToProject("messages.iss", "[CustomMessages]\nMyMsg=Hello\n")
        val main = myFixture.addFileToProject(
            "main.iss",
            "[Setup]\nAppName={cm:Missing}\nAppVersion=1.0\n#include \"messages.iss\"\n",
        )
        open(main)
        assertTrue(
            "A {cm:…} message defined nowhere must still be flagged",
            hasError("Unknown custom message")
        )
    }

    fun testIsppDefineFromIncludeIsNotFlagged() {
        myFixture.addFileToProject("defines.iss", "#define MyVar \"1.0\"\n")
        val main = myFixture.addFileToProject(
            "main.iss",
            "#include \"defines.iss\"\n[Setup]\nAppName=Test\nAppVersion={#MyVar}\n",
        )
        open(main)
        assertFalse(
            "A {#…} constant defined in an included file must not be flagged as unknown",
            hasError("Unknown constant")
        )
    }

    fun testLanguagePrefixDeclaredInIncludeIsNotFlagged() {
        myFixture.addFileToProject(
            "langs.iss",
            "[Languages]\nName: \"de\"; MessagesFile: \"compiler:Default.isl\"\n",
        )
        val main = myFixture.addFileToProject(
            "main.iss",
            "[Setup]\nAppName=Test\nAppVersion=1.0\n#include \"langs.iss\"\n[Messages]\nde.WelcomeLabel1=Willkommen\n",
        )
        open(main)
        assertFalse(
            "A language prefix declared via #include must not be flagged as unknown",
            hasError("Unknown language prefix")
        )
    }
}
