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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsEffectiveScriptProblems]: problems of the combined effective script are attributed to the
 * topmost host `#include` that introduced the offending line; problems of the host file itself are dropped.
 */
class IsEffectiveScriptProblemsTest : IsTimedBasePlatformTestCase() {

    private fun open(name: String, content: String): IsScriptFile {
        val file = myFixture.addFileToProject(name, content)
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        return file as IsScriptFile
    }

    fun testIncludedProblemIsAttributedToTheInclude() {
        myFixture.addFileToProject("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"; Flags: bogusflag\n")
        val main = open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")

        val map = IsEffectiveScriptProblems.forHost(main)
        assertEquals("Exactly one #include must carry problems", 1, map.size)
        assertTrue(
            "The unknown flag must be attributed to the include",
            map.values.flatten().any { it.message.contains("Unknown flag", ignoreCase = true) }
        )
    }

    fun testHostFileProblemsAreDropped() {
        // The unknown flag sits in main's own [Files] line; the included file is clean. No host problem must be
        // attributed to the (clean) include.
        myFixture.addFileToProject("part.iss", "[Icons]\nName: \"{group}\\X\"; Filename: \"{app}\\x.exe\"\n")
        val main = open(
            "main.iss",
            "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n" +
                    "[Files]\nSource: \"a\"; DestDir: \"{app}\"; Flags: bogusflag\n"
        )

        val map = IsEffectiveScriptProblems.forHost(main)
        assertTrue(
            "A problem in the host file's own lines must not be attributed to any #include",
            map.values.flatten().none { it.message.contains("Unknown flag", ignoreCase = true) }
        )
    }

    fun testNestedIncludeProblemMapsToTopmostInclude() {
        // main → part → leaf; the unknown flag lives in leaf but must surface on main's single #include.
        myFixture.addFileToProject("leaf.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"; Flags: bogusflag\n")
        myFixture.addFileToProject("part.iss", "#include \"leaf.iss\"\n")
        val main = open("main.iss", "#include \"part.iss\"\n[Setup]\nAppName=x\nAppVersion=1\n")

        val map = IsEffectiveScriptProblems.forHost(main)
        assertEquals("All nested problems must map to the one topmost include", 1, map.size)
        assertTrue(
            map.values.flatten().any { it.message.contains("Unknown flag", ignoreCase = true) }
        )
    }

    fun testNoIncludeYieldsNoProblems() {
        val main = open(
            "main.iss",
            "[Setup]\nAppName=x\nAppVersion=1\n[Files]\nSource: \"a\"; DestDir: \"{app}\"; Flags: bogusflag\n"
        )
        assertTrue(
            "Without any #include there is no effective analysis",
            IsEffectiveScriptProblems.forHost(main).isEmpty()
        )
    }
}
