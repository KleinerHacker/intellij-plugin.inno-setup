package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiTokenHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = IsiLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(ATTRIBUTES[tokenType])

    companion object {
        // IsiTypes has both PSI element types and token types — use the token types for highlighting
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> =
            hashMapOf<IElementType, TextAttributesKey>().also { m ->
                m[IsiTypes.COMMENT] = IsiSyntaxHighlighting.COMMENT
                m[IsiTypes.QUOTE] = IsiSyntaxHighlighting.STRING
                m[IsiTypes.STRING_PART] = IsiSyntaxHighlighting.STRING
                m[IsiTypes.NUMBER] = IsiSyntaxHighlighting.NUMBER
                m[IsiTypes.LBRACKET] = IsiSyntaxHighlighting.BRACKET
                m[IsiTypes.RBRACKET] = IsiSyntaxHighlighting.BRACKET
                m[IsiTypes.LBRACE] = IsiSyntaxHighlighting.BRACE
                m[IsiTypes.RBRACE] = IsiSyntaxHighlighting.BRACE
                m[IsiTypes.EQ] = IsiSyntaxHighlighting.OPERATION_SIGN
                m[IsiTypes.COLON] = IsiSyntaxHighlighting.OPERATION_SIGN
                m[IsiTypes.SEMICOLON] = IsiSyntaxHighlighting.OPERATION_SIGN
                m[IsiTypes.HASH] = IsiSyntaxHighlighting.KEYWORD
            }
    }
}
