package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

class IsppTokenHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = IsppLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(ATTRIBUTES[tokenType])

    companion object {
        // IsppTypes has both PSI element types and token types — use the token types for highlighting
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> =
            hashMapOf<IElementType, TextAttributesKey>().also { m ->
                m[IsppTypes.QUOTE] = IsppSyntaxHighlighting.STRING
                m[IsppTypes.STRING_PART] = IsppSyntaxHighlighting.STRING
                m[IsppTypes.NUMBER] = IsppSyntaxHighlighting.NUMBER
            }
    }
}
