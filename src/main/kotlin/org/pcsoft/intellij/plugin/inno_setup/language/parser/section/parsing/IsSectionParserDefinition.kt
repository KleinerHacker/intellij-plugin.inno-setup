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
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.parser.IsSectionParser
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

class IsSectionParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IsScriptLanguage)
        val COMMENTS = TokenSet.create(IsSectionTypes.COMMENT)
        val STRINGS = TokenSet.create(IsSectionTypes.QUOTE, IsSectionTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IsSectionLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IsSectionParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IsSectionTypes.Factory.createElement(node)!!

    // Central factory point: the .isl file type reuses this ISS parser but needs the IsLanguageFile PSI so
    // the ISL tooling can tell language files apart from scripts.
    override fun createFile(viewProvider: FileViewProvider): PsiFile =
        if (viewProvider.fileType == IsLanguageFileType.INSTANCE || viewProvider.virtualFile.extension.equals(
                "isl",
                ignoreCase = true
            )
        )
            IsLanguageFile(viewProvider)
        else
            IsScriptFile(viewProvider)
}
