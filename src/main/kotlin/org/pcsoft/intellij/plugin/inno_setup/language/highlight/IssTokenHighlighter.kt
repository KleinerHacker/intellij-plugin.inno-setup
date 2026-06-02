package org.pcsoft.intellij.plugin.inno_setup.language.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.lexer.IssLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssTypes

class IssTokenHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = IssLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(ATTRIBUTES[tokenType])

    companion object {
        // IssTypes has both PSI element types and token types — use the token types for highlighting
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> = hashMapOf<IElementType, TextAttributesKey>().also { m ->
            m[IssTypes.COMMENT]   = DefaultLanguageHighlighterColors.LINE_COMMENT
            m[IssTypes.STRING]    = DefaultLanguageHighlighterColors.STRING
            m[IssTypes.NUMBER]    = DefaultLanguageHighlighterColors.NUMBER
            m[IssTypes.LBRACKET]  = DefaultLanguageHighlighterColors.BRACKETS
            m[IssTypes.RBRACKET]  = DefaultLanguageHighlighterColors.BRACKETS
            m[IssTypes.LBRACE]    = DefaultLanguageHighlighterColors.BRACES
            m[IssTypes.RBRACE]    = DefaultLanguageHighlighterColors.BRACES
            m[IssTypes.EQ]        = DefaultLanguageHighlighterColors.OPERATION_SIGN
            m[IssTypes.COLON]     = DefaultLanguageHighlighterColors.OPERATION_SIGN
            m[IssTypes.SEMICOLON] = DefaultLanguageHighlighterColors.OPERATION_SIGN
            m[IssTypes.HASH]      = DefaultLanguageHighlighterColors.KEYWORD
        }
    }
}
