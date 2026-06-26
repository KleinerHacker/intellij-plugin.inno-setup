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

import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for the `#define` expression completion ([org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDefineExpressionProvider]): inside the
 * value of a `#define`, earlier `#define` names and the predefined ISPP variables are offered.
 */
class IsPreprocessorDefineExpressionCompletionTest : IsTimedBasePlatformTestCase() {

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

    fun testSuggestionsInsideArrayElementIndex() {
        // The `[Index]` of an array element assignment is an expression — references must be offered inside it.
        val variants = expressionLookup(
            "#define First 1\n#dim Arr[3]\n#define Arr[<caret>]\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered inside an array index, was: $variants", "First" in variants)
        assertTrue("Predefined variable must be offered inside an array index, was: $variants", "PREPROCVER" in variants)
    }

    fun testSuggestionsInArrayElementValue() {
        // The value after `#define Name[0]` is an expression — references must be offered there.
        val variants = expressionLookup(
            "#define First 1\n#dim Arr[3]\n#define Arr[0] <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered as an array element value, was: $variants", "First" in variants)
        assertTrue("Predefined variable must be offered as an array element value, was: $variants", "PREPROCVER" in variants)
    }

    fun testSuggestionsInDimInitializer() {
        // Each element of a `#dim Name[Size] { … }` inline initialiser is an expression — references apply.
        val variants = expressionLookup(
            "#define First 1\n#dim Arr[3] { <caret> }\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered in a #dim initializer, was: $variants", "First" in variants)
        assertTrue("Predefined variable must be offered in a #dim initializer, was: $variants", "PREPROCVER" in variants)
    }

    fun testSuggestionsAfterDimSizeBeforeBrace() {
        // Right after `#dim Name[Size]` (before the `{` of the initialiser is typed) references must still come.
        val variants = expressionLookup(
            "#define First 1\n#dim Arr[3] <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered after a #dim size, was: $variants", "First" in variants)
    }

    // ── #if / #elif condition is an expression context ────────────────────────

    fun testSuggestionsInIfCondition() {
        val variants = expressionLookup(
            "#define First 1\n#if <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered in #if, was: $variants", "First" in variants)
        assertTrue("Predefined variable must be offered in #if, was: $variants", "PREPROCVER" in variants)
        assertTrue("Built-in function must be offered in #if, was: $variants", "FileExists" in variants)
    }

    fun testSuggestionsInElifCondition() {
        val variants = expressionLookup(
            "#define First 1\n#if 0\n#elif <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered in #elif, was: $variants", "First" in variants)
    }

    fun testNoExpressionSuggestionsInIfdefName() {
        // #ifdef takes a name, not an expression — the expression providers (predefined variables, built-in
        // functions) must not fire there; only existing #define names are offered (see below).
        val variants = expressionLookup(
            "#define First 1\n#ifdef <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertFalse("Predefined variables must not be offered as an #ifdef name, was: $variants", "PREPROCVER" in variants)
        assertFalse("Built-in functions must not be offered as an #ifdef name, was: $variants", "FileExists" in variants)
    }

    fun testDefineNamesOfferedInIfdefName() {
        // #ifdef/#ifndef reference an existing #define, so earlier define names are offered as the argument.
        val ifdef = expressionLookup(
            "#define First 1\n#ifdef <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Earlier #define must be offered as an #ifdef name, was: $ifdef", "First" in ifdef)

        val ifndef = expressionLookup(
            "#define First 1\n#ifndef <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Earlier #define must be offered as an #ifndef name, was: $ifndef", "First" in ifndef)
    }

    // ── #for {Init; Cond; Incr} Body slots are expression contexts ────────────

    fun testSuggestionsInForCondition() {
        // The condition (2nd slot) references the loop variable and earlier #defines.
        val variants = expressionLookup(
            "#define Limit 10\n#sub Body\n#endsub\n#for {i = 0; i < <caret>; i++} Body\n" +
                "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Loop variable must be offered in the #for condition, was: $variants", "i" in variants)
        assertTrue("Preceding define must be offered in the #for condition, was: $variants", "Limit" in variants)
        assertTrue("Built-in function must be offered in the #for condition, was: $variants", "Len" in variants)
    }

    fun testSuggestionsInForIncrement() {
        // The increment (3rd slot) references the loop variable.
        val variants = expressionLookup(
            "#sub Body\n#endsub\n#for {i = 200; i > 0; i = <caret>} Body\n" +
                "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Loop variable must be offered in the #for increment, was: $variants", "i" in variants)
    }

    fun testSuggestionsInForBodyOfferSubroutine() {
        // The body is usually a #sub call — earlier #sub names must be offered there.
        val variants = expressionLookup(
            "#sub AddFile\n#endsub\n#for {i = 200; i > 0; i--} <caret>\n" +
                "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Subroutine name must be offered as the #for body, was: $variants", "AddFile" in variants)
    }

    fun testSubroutineNotOfferedInDefineExpression() {
        // A #sub may only be called as a #for body — it must not be offered in a #define expression.
        val variants = expressionLookup(
            "#sub AddFile\n#endsub\n#define X <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertFalse("Subroutine must not be offered in a #define expression, was: $variants", "AddFile" in variants)
    }

    fun testSubroutineNotOfferedInForCondition() {
        // The condition slot is not the body — a #sub must not be offered there.
        val variants = expressionLookup(
            "#sub AddFile\n#endsub\n#for {i = 0; <caret>; i++} AddFile\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertFalse("Subroutine must not be offered in the #for condition, was: $variants", "AddFile" in variants)
    }

    fun testSuggestionsInIfExistFilename() {
        // #ifexist takes a string expression, so the expression providers fire (defines + builtins).
        val variants = expressionLookup(
            "#define First 1\n#ifexist <caret>\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Preceding define must be offered in #ifexist, was: $variants", "First" in variants)
        assertTrue("Built-in function must be offered in #ifexist, was: $variants", "FileExists" in variants)
    }
}
