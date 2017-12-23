package org.pcsoft.plugins.intellij.iss.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssConstValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.constant.CommonConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.constant.DirectoryConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.constant.ShellFolderConstantType;

import java.util.Map;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssConstantCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getEnvironmentVariableElementPattern().accepts(element)) {
            handleEnvironmentVariables(completionResultSet);
        } else if (getConstantElementPattern().accepts(element)) {
            handleConstantDirectory(completionResultSet);
            handleConstantShellFolder(completionResultSet);
            handleConstantCommon(completionResultSet);
            completionResultSet.addElement(
                    LookupElementBuilder.create("%")
            );
        }
    }

    //<editor-fold desc="Handlers">
    private void handleConstantDirectory(@NotNull CompletionResultSet completionResultSet) {
        for (final DirectoryConstantType constantType : DirectoryConstantType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(constantType.getName())
                            .withStrikeoutness(constantType.isDeprecated())
                            .withIcon(IssIcons.Constants.Directory)
                            .withInsertHandler((insertionContext, lookupElement) -> handleConstantInsertion(insertionContext, constantType))
            );
        }
    }

    private void handleConstantShellFolder(@NotNull CompletionResultSet completionResultSet) {
        for (final ShellFolderConstantType constantType : ShellFolderConstantType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(constantType.getName())
                            .withStrikeoutness(constantType.isDeprecated())
                            .withIcon(IssIcons.Constants.Shell)
                            .withInsertHandler((insertionContext, lookupElement) -> handleConstantInsertion(insertionContext, constantType))
            );
        }
    }

    private void handleConstantCommon(@NotNull CompletionResultSet completionResultSet) {
        for (final CommonConstantType constantType : CommonConstantType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(constantType.getName())
                            .withStrikeoutness(constantType.isDeprecated())
                            .withInsertHandler((insertionContext, lookupElement) -> handleConstantInsertion(insertionContext, constantType))
            );
        }
    }

    private void handleEnvironmentVariables(@NotNull CompletionResultSet completionResultSet) {
        for (final Map.Entry<String, String> value : System.getenv().entrySet()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(value.getKey())
                            .withTailText(" (" + value.getValue() + ")")
            );
        }
    }

    private void handleConstantInsertion(InsertionContext insertionContext, ConstantType constantType) {
        if (constantType.getArgumentCount() != 0) {
            insertionContext.getDocument().insertString(insertionContext.getTailOffset(), ":");
            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Patterns">
    @NotNull
    private ElementPattern<PsiElement> getConstantElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement(IssCustomTypes.BRACES_CURLY_OPEN)
                                .withParent(IssConstValue.class)
                )
                .withLanguage(IssLanguage.INSTANCE);
    }

    @NotNull
    private ElementPattern<PsiElement> getEnvironmentVariableElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement()
                                .withText("%")
                                .afterLeaf(
                                        PlatformPatterns.psiElement(IssCustomTypes.BRACES_CURLY_OPEN)
                                                .withParent(IssConstValue.class)
                                )
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
    //</editor-fold>
}
