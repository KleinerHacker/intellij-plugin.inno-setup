package org.pcsoft.plugins.intellij.inno_setup.script.structure.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssFormattingModelBuilder implements FormattingModelBuilder {

    @NotNull
    @Override
    public FormattingModel createModel(PsiElement psiElement, CodeStyleSettings codeStyleSettings) {
        return FormattingModelProvider.createFormattingModelForPsiFile(psiElement.getContainingFile(),
                new IssBlock(psiElement.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(),
                        createSpaceBuilder(codeStyleSettings)), codeStyleSettings);
    }

    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings codeStyleSettings) {
        final IssCodeStyleSettings customSettings = codeStyleSettings.getCustomSettings(IssCodeStyleSettings.class);
        return new SpacingBuilder(codeStyleSettings, IssLanguage.INSTANCE)
                .around(IssTokenFactory.OPERATOR_EQUAL).spaceIf(/*customSettings.SPACES_SETUP_ASSIGNER*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .after(IssTokenFactory.OPERATOR_COLON).spaceIf(/*customSettings.SPACES_PROPERTY_ASSIGNER*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(IssTokenFactory.OPERATOR_COLON).none()
                .after(IssTokenFactory.OPERATOR_SEMICOLON).spaceIf(/*customSettings.SPACES_PROPERTY_TERMINATOR*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(IssTokenFactory.OPERATOR_SEMICOLON).none()
                .before(IssTokenFactory.BRACE_BRACKET_START).blankLines(codeStyleSettings.KEEP_BLANK_LINES_IN_CODE)
                .after(IssTokenFactory.BRACE_BRACKET_END).lineBreakInCode();
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
        return null;
    }
}
