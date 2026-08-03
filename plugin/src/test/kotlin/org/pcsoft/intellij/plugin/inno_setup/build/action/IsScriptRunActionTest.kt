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

package org.pcsoft.intellij.plugin.inno_setup.build.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.TestActionEvent
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests the visibility/enabled state computed by [IsScriptRunAction.update].
 */
class IsScriptRunActionTest : IsTimedBasePlatformTestCase() {

    private val action = IsScriptRunAction("Debug")

    private fun update(vararg files: VirtualFile): AnActionEvent {
        val builder = SimpleDataContext.builder().add(CommonDataKeys.PROJECT, project)
        files.firstOrNull()?.let { builder.add(CommonDataKeys.VIRTUAL_FILE, it) }
        builder.add(CommonDataKeys.VIRTUAL_FILE_ARRAY, arrayOf(*files))
        val event = TestActionEvent.createTestEvent(action, builder.build())
        action.update(event)
        return event
    }

    fun testHiddenForNonScript() {
        val txt = myFixture.addFileToProject("readme.txt", "hi").virtualFile
        assertFalse(update(txt).presentation.isVisible)
    }

    fun testEnabledForTopLevelScript() {
        val main = myFixture.addFileToProject("main.iss", "[Setup]\nAppName=x\n").virtualFile
        val p = update(main).presentation
        assertTrue(p.isVisible)
        assertTrue(p.isEnabled)
    }

    fun testDisabledWithReasonForIncludedScript() {
        myFixture.addFileToProject("main.iss", "#include \"part.iss\"\n")
        val part = myFixture.addFileToProject("part.iss", "[Files]\n").virtualFile
        val p = update(part).presentation
        assertTrue(p.isVisible)
        assertFalse(p.isEnabled)
        assertNotNull(p.description)
    }

    fun testHiddenForMultiSelection() {
        val a = myFixture.addFileToProject("a.iss", "[Setup]\n").virtualFile
        val b = myFixture.addFileToProject("b.iss", "[Setup]\n").virtualFile
        assertFalse(update(a, b).presentation.isVisible)
    }
}
