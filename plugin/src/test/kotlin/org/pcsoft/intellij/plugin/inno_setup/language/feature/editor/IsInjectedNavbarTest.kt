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

import com.intellij.injected.editor.VirtualFileWindow
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * With the caret on a `#…` line the element at the caret belongs to the injected ISPP file, whose virtual
 * file is a window into the host. The platform's `DefaultNavBarExtension` presents a file by its virtual file
 * name, which for such a window is `<Injected ISPP file>` — [IsStructureAwareNavbar] must therefore map the
 * fragment back to the host script.
 */
class IsInjectedNavbarTest : IsTimedBasePlatformTestCase() {

    private val navbar = IsStructureAwareNavbar()

    private fun configureInsideInjection() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\n#define De<caret>bug\nAppName=x\n")
    }

    fun testPreconditionCaretIsInsideInjection() {
        configureInsideInjection()

        assertTrue(myFixture.file.virtualFile is VirtualFileWindow)
        assertTrue(
            "The window's name is what the navigation bar would show",
            myFixture.file.virtualFile.presentableName.contains("Injected")
        )
    }

    fun testInjectedFileIsPresentedAsHostScript() {
        configureInsideInjection()
        val hostName = (navbar.adjustElement(myFixture.file) as IsScriptFile).name

        assertEquals(hostName, navbar.getPresentableText(myFixture.file))
        assertFalse(
            "No injected-fragment name in the navigation bar",
            navbar.getPresentableText(myFixture.file).orEmpty().contains("Injected")
        )
    }

    fun testInjectedElementIsAdjustedToItsHost() {
        configureInsideInjection()
        val element = myFixture.file.findElementAt(myFixture.caretOffset)!!

        val adjusted = navbar.adjustElement(element)
        assertFalse(
            "The adjusted element must live in the host script",
            adjusted.containingFile.virtualFile is VirtualFileWindow
        )
    }

    fun testPlainScriptElementsAreUntouched() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nApp<caret>Name=x\n")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)!!

        assertSame(element, navbar.adjustElement(element))
    }
}
