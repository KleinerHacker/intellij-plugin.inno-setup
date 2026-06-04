package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

class IsppSyntaxHighlighting : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = IsppLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        IsppTypes.QUOTE, IsppTypes.STRING_PART -> pack(DefaultLanguageHighlighterColors.STRING)
        IsppTypes.NUMBER                       -> pack(DefaultLanguageHighlighterColors.NUMBER)
        else                                   -> emptyArray()
    }
}
