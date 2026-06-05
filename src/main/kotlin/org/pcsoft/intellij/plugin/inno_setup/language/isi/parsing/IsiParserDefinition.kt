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
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.parser.IsiParser
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IssLanguage)
        val COMMENTS = TokenSet.create(IsiTypes.COMMENT)
        val STRINGS  = TokenSet.create(IsiTypes.QUOTE, IsiTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IsiLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IsiParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IsiTypes.Factory.createElement(node)!!
    override fun createFile(viewProvider: FileViewProvider): PsiFile = IssFile(viewProvider)
}
