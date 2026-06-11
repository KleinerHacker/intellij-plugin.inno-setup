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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

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
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.isl.IslFile
import org.pcsoft.intellij.plugin.inno_setup.language.isl.IslFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.parser.IsiParser
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IssLanguage)
        val COMMENTS = TokenSet.create(IsiTypes.COMMENT)
        val STRINGS = TokenSet.create(IsiTypes.QUOTE, IsiTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IsiLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IsiParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IsiTypes.Factory.createElement(node)!!
    // Central factory point: the .isl file type reuses this ISS parser but needs the IslFile PSI so
    // the ISL tooling can tell language files apart from scripts.
    override fun createFile(viewProvider: FileViewProvider): PsiFile =
        if (viewProvider.fileType == IslFileType.INSTANCE || viewProvider.virtualFile.extension.equals("isl", ignoreCase = true))
            IslFile(viewProvider)
        else
            IssFile(viewProvider)
}
