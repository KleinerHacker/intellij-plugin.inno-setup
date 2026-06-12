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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

/**
 * Maps lexer token types to editor text attributes.
 */
class IsSectionTokenHighlighter : SyntaxHighlighterBase() {

    /**
     * Creates the lexer used for syntax highlighting.
     */
    override fun getHighlightingLexer(): Lexer = IsSectionLexerAdapter()

    /**
     * Returns text attributes for the supplied token type.
     */
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        pack(ATTRIBUTES[tokenType])

    companion object {
        // IsSectionTypes has both PSI element types and token types — use the token types for highlighting
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> =
            hashMapOf<IElementType, TextAttributesKey>().also { m ->
                m[IsSectionTypes.COMMENT] = IsSectionSyntaxHighlighting.COMMENT
                m[IsSectionTypes.QUOTE] = IsSectionSyntaxHighlighting.STRING
                m[IsSectionTypes.STRING_PART] = IsSectionSyntaxHighlighting.STRING
                m[IsSectionTypes.NUMBER] = IsSectionSyntaxHighlighting.NUMBER
                m[IsSectionTypes.LBRACKET] = IsSectionSyntaxHighlighting.BRACKET
                m[IsSectionTypes.RBRACKET] = IsSectionSyntaxHighlighting.BRACKET
                m[IsSectionTypes.LBRACE] = IsSectionSyntaxHighlighting.BRACE
                m[IsSectionTypes.RBRACE] = IsSectionSyntaxHighlighting.BRACE
                m[IsSectionTypes.EQ] = IsSectionSyntaxHighlighting.OPERATION_SIGN
                m[IsSectionTypes.COLON] = IsSectionSyntaxHighlighting.OPERATION_SIGN
                m[IsSectionTypes.SEMICOLON] = IsSectionSyntaxHighlighting.OPERATION_SIGN
                m[IsSectionTypes.HASH] = IsSectionSyntaxHighlighting.KEYWORD
            }
    }
}
