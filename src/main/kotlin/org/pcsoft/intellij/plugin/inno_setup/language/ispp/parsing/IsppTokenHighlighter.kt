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
