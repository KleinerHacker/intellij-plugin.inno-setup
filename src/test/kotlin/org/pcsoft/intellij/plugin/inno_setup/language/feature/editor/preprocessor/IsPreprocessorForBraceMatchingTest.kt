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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.codeInsight.highlighting.BraceMatchingUtil
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorTokenHighlighter
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Verifies that the `{ … }` group of a `#for` is brace-matched by the ISPP brace matcher: a lexer iterator over
 * the injected `#for` text pairs the opening `{` with its closing `}`.
 *
 * Brace matching for the `#for` braces can only come from the **injected ISPP** fragment: in the section host
 * the whole `#…` line is a single opaque `HASH_LINE` token, so the host editor never sees the braces.
 */
class IsPreprocessorForBraceMatchingTest : IsTimedBasePlatformTestCase() {

    private fun iteratorAt(text: String, offset: Int): Pair<CharSequence, HighlighterIterator> {
        val highlighter = LexerEditorHighlighter(
            IsPreprocessorTokenHighlighter(),
            com.intellij.openapi.editor.colors.EditorColorsManager.getInstance().globalScheme,
        )
        highlighter.setText(text)
        return text to highlighter.createIterator(offset)
    }

    fun testForOpeningBraceMatchesClosingBrace() {
        // The injected ISPP text of a `#for {i = 200; i > 0; i--} AddFile` line.
        val text = "#for {i = 200; i > 0; i--} AddFile"
        val (chars, iterator) = iteratorAt(text, text.indexOf('{'))
        val matched = BraceMatchingUtil.matchBrace(chars, IsScriptFileType.INSTANCE, iterator, true)
        assertTrue("The #for opening '{' must be matched with its closing '}'", matched)
    }

    fun testForClosingBraceMatchesOpeningBrace() {
        val text = "#for {i = 200; i > 0; i--} AddFile"
        val (chars, iterator) = iteratorAt(text, text.indexOf('}'))
        val matched = BraceMatchingUtil.matchBrace(chars, IsScriptFileType.INSTANCE, iterator, false)
        assertTrue("The #for closing '}' must be matched backward with its opening '{'", matched)
    }

    fun testForBraceAutoClosesWhenTyped() {
        // Typing `{` right after `#for ` must insert the matching `}` (paired-brace auto-insert).
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#for <caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n",
        )
        myFixture.type("{")
        myFixture.checkResult("#for {<caret>}\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
    }
}
