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
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.parser.IsppParser
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

class IsppParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IsppLanguage)
        val STRINGS = TokenSet.create(IsppTypes.QUOTE, IsppTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IsppLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IsppParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IsppTypes.Factory.createElement(node)!!
    override fun createFile(viewProvider: FileViewProvider): PsiFile = IsppFile(viewProvider)
}
