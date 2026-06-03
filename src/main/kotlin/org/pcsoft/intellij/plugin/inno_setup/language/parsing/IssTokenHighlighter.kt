package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssTokenHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = IssLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(ATTRIBUTES[tokenType])

    companion object {
        // IssTypes has both PSI element types and token types — use the token types for highlighting
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> = hashMapOf<IElementType, TextAttributesKey>().also { m ->
            m[IssTypes.COMMENT]      = IssSyntaxHighlighting.COMMENT
            m[IssTypes.QUOTE]        = IssSyntaxHighlighting.STRING
            m[IssTypes.STRING_PART]  = IssSyntaxHighlighting.STRING
            m[IssTypes.NUMBER]       = IssSyntaxHighlighting.NUMBER
            m[IssTypes.LBRACKET]  = IssSyntaxHighlighting.BRACKET
            m[IssTypes.RBRACKET]  = IssSyntaxHighlighting.BRACKET
            m[IssTypes.LBRACE]    = IssSyntaxHighlighting.BRACE
            m[IssTypes.RBRACE]    = IssSyntaxHighlighting.BRACE
            m[IssTypes.EQ]        = IssSyntaxHighlighting.OPERATION_SIGN
            m[IssTypes.COLON]     = IssSyntaxHighlighting.OPERATION_SIGN
            m[IssTypes.SEMICOLON] = IssSyntaxHighlighting.OPERATION_SIGN
            m[IssTypes.HASH]      = IssSyntaxHighlighting.KEYWORD
        }
    }
}
