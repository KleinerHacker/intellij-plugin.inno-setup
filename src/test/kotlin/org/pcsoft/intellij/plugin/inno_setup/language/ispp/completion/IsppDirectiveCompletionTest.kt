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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp.completion

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for the directive-keyword completion contributed by
 * `IsppDirectiveKeywordProvider` inside [IsppCompletionContributor]:
 * after `#`, the ISPP directive names from the spec are offered.
 */
class IsppDirectiveCompletionTest : BasePlatformTestCase() {

    private fun directiveLookup(content: String): List<String> {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testDirectivesAreSuggestedAfterHash() {
        val variants = directiveLookup("#<caret>\n")
        assertTrue("Expected 'define' directive suggestion, was: $variants", "define" in variants)
        assertTrue("Expected 'include' directive suggestion, was: $variants", "include" in variants)
    }

    fun testEachDirectiveIsSuggestedOnce() {
        val variants = directiveLookup("#<caret>\n")
        assertEquals("'define' must be offered exactly once", 1, variants.count { it == "define" })
    }

    fun testDirectiveCompletionInsertsTrailingSpace() {
        myFixture.configureByText(IssFileType.INSTANCE, "#<caret>\n")
        val items = myFixture.completeBasic() ?: emptyArray()
        val define = items.firstOrNull { it.lookupString == "define" }
        assertNotNull("Expected a 'define' lookup element", define)
        // Select 'define' explicitly; the provider's insert handler appends a space.
        myFixture.lookup.currentItem = define
        myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        assertTrue(
            "Expected '#define ' after completion, was: '${myFixture.editor.document.text.trim()}'",
            myFixture.editor.document.text.startsWith("#define ")
        )
    }
}
