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
        val FILE     = IFileElementType(IsppLanguage)
        val STRINGS  = TokenSet.create(IsppTypes.QUOTE, IsppTypes.STRING_PART)
    }

    override fun createLexer(project: Project?): Lexer = IsppLexerAdapter()
    override fun createParser(project: Project?): PsiParser = IsppParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun createElement(node: ASTNode): PsiElement = IsppTypes.Factory.createElement(node)!!
    override fun createFile(viewProvider: FileViewProvider): PsiFile = IsppFile(viewProvider)
}
