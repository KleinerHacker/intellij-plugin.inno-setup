package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

/**
 * Numbers and strings inside a #define expression must be highlighted like everywhere else in the
 * script: number literals in the NUMBER color, quoted strings in the STRING color.
 */
class IsppTokenHighlighterTest : BasePlatformTestCase() {

    private val highlighter = IsppTokenHighlighter()

    fun testNumberLiteralUsesNumberColor() {
        assertTrue(
            "NUMBER token in a #define expression must use the NUMBER color",
            highlighter.getTokenHighlights(IsppTypes.NUMBER)
                .contains(DefaultLanguageHighlighterColors.NUMBER)
        )
    }

    fun testQuotedStringUsesStringColor() {
        assertTrue(
            "Opening/closing quote must use the STRING color",
            highlighter.getTokenHighlights(IsppTypes.QUOTE)
                .contains(DefaultLanguageHighlighterColors.STRING)
        )
        assertTrue(
            "String content must use the STRING color",
            highlighter.getTokenHighlights(IsppTypes.STRING_PART)
                .contains(DefaultLanguageHighlighterColors.STRING)
        )
    }
}
