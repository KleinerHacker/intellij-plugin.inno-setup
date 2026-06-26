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
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for the `#pragma` completion contributed by `IsPreprocessorPragmaProvider`: after `#pragma ` the
 * sub-commands are offered, and after `#pragma option|parseroption ` the accepted option flags.
 */
class IsPreprocessorPragmaCompletionTest : IsTimedBasePlatformTestCase() {

    private fun lookup(content: String, fileType: FileType = IsScriptFileType.INSTANCE): List<String> {
        myFixture.configureByText(fileType, content)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testSubCommandsAreSuggestedAfterPragma() {
        val variants = lookup("#pragma <caret>\n")
        assertTrue("Expected 'option' suggestion, was: $variants", "option" in variants)
        assertTrue("Expected 'verboselevel' suggestion, was: $variants", "verboselevel" in variants)
        assertTrue("Expected 'message' suggestion, was: $variants", "message" in variants)
    }

    fun testSubCommandCompletionInsertsTrailingSpace() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#pragma <caret>\n")
        val items = myFixture.completeBasic() ?: emptyArray()
        val option = items.firstOrNull { it.lookupString == "option" }
        assertNotNull("Expected an 'option' lookup element", option)
        myFixture.lookup.currentItem = option
        myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        assertTrue(
            "Expected '#pragma option ' after completion, was: '${myFixture.editor.document.text.trim()}'",
            myFixture.editor.document.text.startsWith("#pragma option ")
        )
    }

    fun testOptionFlagsAreSuggested() {
        val variants = lookup("#pragma option <caret>\n")
        assertTrue("Expected '-v+' flag suggestion, was: $variants", "-v+" in variants)
        assertTrue("Expected '-c-' flag suggestion, was: $variants", "-c-" in variants)
        assertFalse("parseroption flag '-u+' must not appear for option, was: $variants", "-u+" in variants)
    }

    fun testParserOptionFlagsAreSuggested() {
        val variants = lookup("#pragma parseroption <caret>\n")
        assertTrue("Expected '-b+' flag suggestion, was: $variants", "-b+" in variants)
        assertTrue("Expected '-u-' flag suggestion, was: $variants", "-u-" in variants)
        assertFalse("option flag '-c+' must not appear for parseroption, was: $variants", "-c+" in variants)
    }

    fun testPragmaNotSuggestedInIslFiles() {
        val variants = lookup(
            "#pragma <caret>\n[LangOptions]\nLanguageName=English\nLanguageID=\$0409\n",
            IsLanguageFileType.INSTANCE
        )
        assertFalse("ISL files must not suggest pragma sub-commands, was: $variants", "option" in variants)
    }
}
