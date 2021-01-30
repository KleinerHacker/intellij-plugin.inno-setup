package org.pcsoft.plugins.intellij.iss.core.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.core.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssConstName;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PreprocessorType;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssPreprocessorCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getPreprocessorElementPattern().accepts(element)) {
            handlePreprocessor(completionResultSet, element);
        }
    }

    private void handlePreprocessor(@NotNull CompletionResultSet completionResultSet, PsiElement element) {
        for (final PreprocessorType preprocessorType : org.pcsoft.plugins.intellij.iss.core.language.type.PreprocessorType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(preprocessorType.getName())
                            .withItemTextForeground(IssHighlighting.PREPROCESSOR.getDefaultAttributes().getForegroundColor())
            );
        }
    }

    @NotNull
    private ElementPattern<PsiElement> getPreprocessorElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement()
                                .withText("#")
                                .without(new PatternCondition<>("Without Const Name") {
                                    @Override
                                    public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext processingContext) {
                                        return psiElement.getParent() instanceof IssConstName;
                                    }
                                })
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
}
