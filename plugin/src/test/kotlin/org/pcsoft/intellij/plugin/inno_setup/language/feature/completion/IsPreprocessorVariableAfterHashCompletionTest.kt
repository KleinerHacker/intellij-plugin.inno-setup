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

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests that value-bearing ISPP predefined variables are offered after `{#` (inline emission),
 * while user `#define`s still appear and the valueless `void` symbols are excluded.
 */
class IsPreprocessorVariableAfterHashCompletionTest : IsTimedBasePlatformTestCase() {

    private fun lookup(content: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testValueBearingPredefinedVariablesSuggestedAfterHashBrace() {
        val variants = lookup("[Files]\nSource: \"a\"; DestDir: \"{#<caret>\"\n")
        assertTrue("Expected 'SourcePath', was: $variants", "SourcePath" in variants)
        assertTrue("Expected '__LINE__', was: $variants", "__LINE__" in variants)
    }

    fun testVoidSymbolsAreNotSuggestedAfterHashBrace() {
        val variants = lookup("[Files]\nSource: \"a\"; DestDir: \"{#<caret>\"\n")
        assertFalse("void symbol 'WINDOWS' must not be offered for {#…}, was: $variants", "WINDOWS" in variants)
        assertFalse("void symbol 'ISPP_INVOKED' must not be offered, was: $variants", "ISPP_INVOKED" in variants)
    }

    fun testUserDefinesStillSuggestedAfterHashBrace() {
        val variants = lookup("#define MyConst \"1.0\"\n[Files]\nSource: \"a\"; DestDir: \"{#<caret>\"\n")
        assertTrue("User #define 'MyConst' must still be offered, was: $variants", "MyConst" in variants)
    }
}
