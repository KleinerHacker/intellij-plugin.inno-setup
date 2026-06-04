package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

/**
 * Numbers and strings inside a #define expression must be highlighted like everywhere else in the
 * script: number literals in the NUMBER color, quoted strings in the STRING color.
 *
 * The tests drive the same highlighting lexer the editor uses for the injected ISPP fragment, so
 * they verify both that the lexer produces the expected tokens and that they map to the right color.
 */
class IsppTokenHighlighterTest : BasePlatformTestCase() {

    private val highlighter = IsppTokenHighlighter()

    /** Lexes [text] with the highlighting lexer and returns the (tokenType, tokenText) pairs. */
    private fun lex(text: String): List<Pair<IElementType, String>> {
        val lexer = highlighter.highlightingLexer
        val tokens = mutableListOf<Pair<IElementType, String>>()
        lexer.start(text)
        while (lexer.tokenType != null) {
            tokens += lexer.tokenType!! to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        return tokens
    }

    /** Asserts that the substring [part] of [text] is lexed as a single token coloured with [color]. */
    private fun assertHighlighted(text: String, part: String, color: com.intellij.openapi.editor.colors.TextAttributesKey) {
        val token = lex(text).firstOrNull { it.second == part }
        assertNotNull("'$part' in `$text` must be a single token", token)
        assertTrue(
            "'$part' in `$text` must be highlighted with ${color.externalName}",
            highlighter.getTokenHighlights(token!!.first).contains(color)
        )
    }

    // ── Color mapping for the individual token types ──────────────────────────

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

    // ── End-to-end highlighting inside a #define line ─────────────────────────

    fun testIntegerInDefineIsHighlightedAsNumber() {
        assertHighlighted("#define Build 42", "42", DefaultLanguageHighlighterColors.NUMBER)
    }

    fun testFloatInDefineIsHighlightedAsNumber() {
        assertHighlighted("#define Ratio 1.7", "1.7", DefaultLanguageHighlighterColors.NUMBER)
    }

    fun testStringInDefineIsHighlightedAsString() {
        val text = "#define Title \"Hello\""
        assertHighlighted(text, "\"", DefaultLanguageHighlighterColors.STRING)
        assertHighlighted(text, "Hello", DefaultLanguageHighlighterColors.STRING)
    }
}
