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

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for the `#define` expression completion ([IsPreprocessorDefineExpressionProvider]): inside the
 * value of a `#define`, earlier `#define` names and the predefined ISPP variables are offered.
 */
class IsPreprocessorDefineExpressionCompletionTest : BasePlatformTestCase() {

    private fun expressionLookup(content: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testPredefinedVariablesSuggestedInDefineExpression() {
        val variants = expressionLookup("#define Second <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertTrue("Expected predefined variable '__LINE__', was: $variants", "__LINE__" in variants)
        assertTrue("Expected predefined variable 'PREPROCVER', was: $variants", "PREPROCVER" in variants)
    }

    fun testPrecedingDefinesStillSuggested() {
        val variants = expressionLookup(
            "#define First 1\n#define Second <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Expected preceding define 'First', was: $variants", "First" in variants)
        assertTrue("Predefined variables must still be offered alongside, was: $variants", "__LINE__" in variants)
    }

    fun testPredefinedVariablesNotSuggestedOutsideExpression() {
        // Caret right after the directive keyword (still typing the name) — not the expression part.
        val variants = expressionLookup("#define <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertFalse("Predefined variables must not be offered as a #define name, was: $variants", "__LINE__" in variants)
    }
}
