package org.pcsoft.plugins.intellij.inno_setup.script.structure.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
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
                        createSPaceBuilder(codeStyleSettings)), codeStyleSettings);
    }

    private static SpacingBuilder createSPaceBuilder(CodeStyleSettings codeStyleSettings) {
        final IssCodeStyleSettings customSettings = codeStyleSettings.getCustomSettings(IssCodeStyleSettings.class);
        return new SpacingBuilder(codeStyleSettings, IssLanguage.INSTANCE)
                .around(IssTokenFactory.OPERATOR_EQUAL).spaceIf(/*customSettings.SPACES_SETUP_ASSIGNER*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .after(IssTokenFactory.OPERATOR_COLON).spaceIf(/*customSettings.SPACES_PROPERTY_ASSIGNER*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(IssTokenFactory.OPERATOR_COLON).none()
                .after(IssTokenFactory.OPERATOR_SEMICOLON).spaceIf(/*customSettings.SPACES_PROPERTY_TERMINATOR*/ codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .before(IssTokenFactory.OPERATOR_SEMICOLON).none()
                .before(IssMarkerFactory.SECTION_TITLE).none();
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode) {
        return null;
    }
}
