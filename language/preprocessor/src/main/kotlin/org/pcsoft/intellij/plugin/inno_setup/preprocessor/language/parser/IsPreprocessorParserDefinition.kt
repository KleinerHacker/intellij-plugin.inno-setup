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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorTypes

/**
 * Defines lexer, parser, PSI file, and token sets for this Inno Setup language.
 */
class IsPreprocessorParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IsPreprocessorLanguage)
        val STRINGS = TokenSet.create(IsPreprocessorTypes.QUOTE, IsPreprocessorTypes.STRING_PART)
    }

    /**
     * Creates the lexer used by the parser definition.
     */
    override fun createLexer(project: Project?): Lexer = IsPreprocessorLexerAdapter()

    /**
     * Creates the parser used by the parser definition.
     */
    override fun createParser(project: Project?): PsiParser = IsPreprocessorParser()

    /**
     * Returns token metadata required by the parser definition.
     */
    override fun getFileNodeType(): IFileElementType = FILE

    /**
     * Returns token metadata required by the parser definition.
     */
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

    /**
     * Returns token metadata required by the parser definition.
     */
    override fun getStringLiteralElements(): TokenSet = STRINGS

    /**
     * Creates a PSI element for the supplied AST node.
     */
    override fun createElement(node: ASTNode): PsiElement = IsPreprocessorTypes.Factory.createElement(node)!!

    /**
     * Creates the PSI file instance for the supplied view provider.
     */
    override fun createFile(viewProvider: FileViewProvider): PsiFile = IsPreprocessorFile(viewProvider)
}
