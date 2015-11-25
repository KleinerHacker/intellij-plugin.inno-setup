package org.pcsoft.plugins.intellij.inno_setup.script.parser;

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
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguageFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssScriptFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssLexerAdapter;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssLanguageFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssPsiElementFactory;

/**
 * Created by Christoph on 12.12.2014.
 */
public class IssParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(Language.findInstance(IssLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return IssLexerAdapter.INSTANCE;
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
        return IssTokenFactory.TS_WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return IssTokenFactory.TS_COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return IssPsiElementFactory.create(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        if (fileViewProvider.getFileType().getClass() == IssScriptFileType.class)
            return new IssScriptFile(fileViewProvider);
        else if (fileViewProvider.getFileType().getClass() == IssLanguageFileType.class)
            return new IssLanguageFile(fileViewProvider);

        throw new RuntimeException();
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }
}
