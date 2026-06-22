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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.openapi.fileTypes.FileType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for the directive-keyword completion contributed by
 * `IsPreprocessorDirectiveKeywordProvider` inside [IsPreprocessorCompletionContributor]:
 * after `#`, the ISPP directive names from the spec are offered.
 */
class IsPreprocessorDirectiveCompletionTest : BasePlatformTestCase() {

    private fun directiveLookup(content: String, fileType: FileType = IsScriptFileType.INSTANCE): List<String> {
        myFixture.configureByText(fileType, content)
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

    fun testDirectivesAreNotSuggestedInIslFiles() {
        val variants = directiveLookup(
            "#<caret>\n[LangOptions]\nLanguageName=English\nLanguageID=\$0409\n",
            IsLanguageFileType.INSTANCE
        )
        assertFalse("ISL files must not suggest 'define' preprocessor directive, was: $variants", "define" in variants)
        assertFalse(
            "ISL files must not suggest 'include' preprocessor directive, was: $variants",
            "include" in variants
        )
    }

    fun testDirectiveCompletionInsertsTrailingSpace() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#<caret>\n")
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
