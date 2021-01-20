package org.pcsoft.plugins.intellij.iss.language.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.parser.lexer.IssLexerAdapter;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssParserDefinition implements ParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(Language.findInstance(IssLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new IssLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new IssParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return IssCustomTypes.WHITE_SPACE_SET;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return IssCustomTypes.COMMENT_SET;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return IssGenTypes.Factory.createElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new IssFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }
}
