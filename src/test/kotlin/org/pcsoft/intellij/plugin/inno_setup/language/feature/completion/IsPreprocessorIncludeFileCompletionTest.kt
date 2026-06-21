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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests the `#include` file-name completion: project `*.iss` files are offered by name and inserted as the
 * path relative to the directory of the edited script.
 */
class IsPreprocessorIncludeFileCompletionTest : BasePlatformTestCase() {

    fun testIncludeCompletionInsertsRelativePath() {
        myFixture.addFileToProject("lib/dep.iss", "[Files]\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"\"\n")
        myFixture.configureFromExistingVirtualFile(main.virtualFile)
        // Place the caret between the two quotes.
        myFixture.editor.caretModel.moveToOffset(main.text.indexOf("\"\"") + 1)

        // A single candidate ('main.iss' excludes itself) auto-inserts and returns null; otherwise pick it.
        val items = myFixture.completeBasic()
        if (items != null) {
            val dep = items.firstOrNull { it.lookupString == "dep.iss" }
            assertNotNull("Expected 'dep.iss' to be offered, was: ${items.map { it.lookupString }}", dep)
            myFixture.lookup.currentItem = dep
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        assertTrue(
            "Include must be inserted relative to the edited file, was: '${myFixture.editor.document.text.trim()}'",
            myFixture.editor.document.text.contains("#include \"lib/dep.iss\"")
        )
    }

    fun testTemplateFilesAreOffered() {
        myFixture.addFileToProject("lib/part.ist", "[Files]\n")
        myFixture.addFileToProject("lib/other.iss", "[Files]\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"\"\n")
        myFixture.configureFromExistingVirtualFile(main.virtualFile)
        myFixture.editor.caretModel.moveToOffset(main.text.indexOf("\"\"") + 1)

        val items = myFixture.completeBasic()
        // More than one candidate, so completion does not auto-insert and returns the list.
        val strings = items?.map { it.lookupString } ?: emptyList()
        assertTrue("Expected the .ist template to be offered, was: $strings", "part.ist" in strings)
    }
}
