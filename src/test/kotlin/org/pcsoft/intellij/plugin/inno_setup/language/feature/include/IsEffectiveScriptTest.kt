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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections

/**
 * Tests for [toEffectiveScript]: the fully `#include`-resolved view of a script.
 */
class IsEffectiveScriptTest : BasePlatformTestCase() {

    private fun script(name: String, content: String): IsScriptFile =
        myFixture.addFileToProject(name, content) as IsScriptFile

    private fun sectionNames(file: IsScriptFile) = file.sections.map { it.nameText.lowercase() }

    fun testIncludedSectionsAreMerged() {
        script("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        val main = script("main.iss", "[Setup]\nAppName=x\n#include \"part.iss\"\n")

        val effective = main.toEffectiveScript()
        assertTrue("Effective script must contain the main [Setup]", "setup" in sectionNames(effective))
        assertTrue("Effective script must contain the included [Files]", "files" in sectionNames(effective))
    }

    fun testTransitiveIncludesAreResolved() {
        script("c.iss", "[Tasks]\nName: t; Description: \"d\"\n")
        script("b.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n#include \"c.iss\"\n")
        val main = script("a.iss", "[Setup]\nAppName=x\n#include \"b.iss\"\n")

        val names = sectionNames(main.toEffectiveScript())
        assertTrue("Transitive include must contribute [Tasks]", "tasks" in names)
        assertTrue("Direct include must contribute [Files]", "files" in names)
    }

    fun testIncludeCycleTerminates() {
        script("b.iss", "[Files]\n#include \"a.iss\"\n")
        val main = script("a.iss", "[Setup]\nAppName=x\n#include \"b.iss\"\n")

        // Must not loop forever; both sections present, cycle broken.
        val names = sectionNames(main.toEffectiveScript())
        assertTrue("setup" in names)
        assertTrue("files" in names)
    }

    fun testMissingIncludeIsLeftUntouched() {
        val main = script("main.iss", "[Setup]\nAppName=x\n#include \"does-not-exist.iss\"\n")
        // No crash, original section still present.
        assertTrue("setup" in sectionNames(main.toEffectiveScript()))
    }

    fun testSameNamedSectionsAreMergedInEffectiveView() {
        script("part.iss", "[Files]\nSource: \"b\"; DestDir: \"{app}\"\n")
        // [Files] appears in main and again (via include) — the effective view must unify them into one block.
        val main = script(
            "main.iss",
            "[Setup]\nAppName=x\n[Files]\nSource: \"a\"; DestDir: \"{app}\"\n#include \"part.iss\"\n"
        )

        val effective = main.toEffectiveScript()
        val fileBlocks = effective.sections.filter { it.nameText.equals("Files", ignoreCase = true) }
        assertEquals("All [Files] sections must be merged into one", 1, fileBlocks.size)
        val text = fileBlocks.single().text
        assertTrue("Merged [Files] must keep the main entry", text.contains("\"a\""))
        assertTrue("Merged [Files] must contain the included entry", text.contains("\"b\""))
    }

    fun testExpressionIncludeIsNotResolved() {
        script("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        // An expression-based include is not a literal string → not inlined.
        val main = script("main.iss", "#define P \"part.iss\"\n[Setup]\nAppName=x\n#include P\n")
        assertFalse("Expression include must not be inlined", "files" in sectionNames(main.toEffectiveScript()))
    }
}
