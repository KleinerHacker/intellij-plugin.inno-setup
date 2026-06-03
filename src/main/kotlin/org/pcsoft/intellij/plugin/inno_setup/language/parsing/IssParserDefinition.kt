package org.pcsoft.intellij.plugin.inno_setup.language.parsing

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
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.parser.IssParser
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(IssLanguage)
        val COMMENTS = TokenSet.create(IssTypes.COMMENT)
        val STRINGS  = TokenSet.create(IssTypes.QUOTE, IssTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IssLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IssParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IssTypes.Factory.createElement(node)!!
    override fun createFile(viewProvider: FileViewProvider): PsiFile = IssFile(viewProvider)
}
