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

import com.intellij.codeInsight.lookup.LookupElementPresentation
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

    private fun presentationOf(content: String, lookupString: String): LookupElementPresentation? {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        myFixture.completeBasic()
        val element = myFixture.lookupElements?.firstOrNull { it.lookupString == lookupString } ?: return null
        return LookupElementPresentation.renderElement(element)
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

    fun testBuiltinFunctionsSuggestedInDefineExpression() {
        val variants = expressionLookup("#define Second <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertTrue("Expected built-in function 'Len', was: $variants", "Len" in variants)
        assertTrue("Expected built-in function 'FileExists', was: $variants", "FileExists" in variants)
    }

    fun testBuiltinFunctionsNotSuggestedAsDefineName() {
        val variants = expressionLookup("#define <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertFalse("Built-in functions must not be offered as a #define name, was: $variants", "Len" in variants)
    }

    fun testFunctionMacroSuggestedAsFunction() {
        val content = "#define Mul(a, b) a * b\n#define Second <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        assertTrue("Function-like macro must be offered", "Mul" in expressionLookup(content))
        val presentation = presentationOf(content, "Mul")
        assertNotNull("Expected a lookup element for 'Mul'", presentation)
        assertEquals("Function-like macro must show its parameter list", "(a, b)", presentation!!.tailText)
        assertEquals("Function-like macro must be typed as a macro", "macro", presentation.typeText)
    }

    fun testNoExpressionSuggestionsInsideString() {
        // Inside a string literal of the expression the text is plain, not a reference — nothing is offered.
        val variants = expressionLookup(
            "#define First 1\n#define Second \"abc <caret>\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertFalse("Preceding defines must not be offered inside a string, was: $variants", "First" in variants)
        assertFalse("Predefined variables must not be offered inside a string, was: $variants", "__LINE__" in variants)
        assertFalse("Built-in functions must not be offered inside a string, was: $variants", "Len" in variants)
    }

    fun testExpressionSuggestionsAfterClosedString() {
        // After a closed string (even number of quotes) the caret is back in expression context.
        val variants = expressionLookup(
            "#define First 1\n#define Second \"abc\" + <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Defines must be offered again after a closed string, was: $variants", "First" in variants)
    }

    fun testPlainDefineSuggestedAsDefine() {
        val content = "#define First 1\n#define Second <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val presentation = presentationOf(content, "First")
        assertNotNull("Expected a lookup element for 'First'", presentation)
        assertEquals("A plain value define must be typed as a define", "define", presentation!!.typeText)
    }
}
