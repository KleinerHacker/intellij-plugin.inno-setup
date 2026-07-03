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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include.action

import com.intellij.openapi.fileEditor.FileEditorManager
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsShowEffectiveScriptAction]: opening the effective script produces a read-only, in-memory tab
 * whose content is the fully `#include`-resolved script.
 */
class IsShowEffectiveScriptActionTest : IsTimedBasePlatformTestCase() {

    fun testEffectiveScriptOpensReadOnlyWithMergedContent() {
        myFixture.addFileToProject("part.iss", "[Files]\nSource: \"a\"; DestDir: \"{app}\"\n")
        val host = myFixture.addFileToProject(
            "main.iss",
            "[Setup]\nAppName=x\n#include \"part.iss\"\n"
        ) as IsScriptFile

        val vFile = IsShowEffectiveScriptAction.openEffectiveScript(project, host)
        assertNotNull("An effective-script tab must be opened", vFile)
        assertFalse("The effective-script tab must be read-only", vFile!!.isWritable)

        val text = String(vFile.contentsToByteArray(), vFile.charset)
        assertTrue("Effective content must keep the host [Setup]", text.contains("[Setup]"))
        assertTrue("Effective content must inline the included [Files]", text.contains("[Files]"))

        assertTrue(
            "The effective tab must be open in the editor",
            FileEditorManager.getInstance(project).openFiles.any { it === vFile }
        )
    }

    fun testEffectiveScriptWithoutIncludeMirrorsOriginal() {
        val host = myFixture.addFileToProject("main.iss", "[Setup]\nAppName=x\n") as IsScriptFile

        val vFile = IsShowEffectiveScriptAction.openEffectiveScript(project, host)
        assertNotNull(vFile)
        assertFalse(vFile!!.isWritable)
        assertEquals(host.text, String(vFile.contentsToByteArray(), vFile.charset))
    }
}
