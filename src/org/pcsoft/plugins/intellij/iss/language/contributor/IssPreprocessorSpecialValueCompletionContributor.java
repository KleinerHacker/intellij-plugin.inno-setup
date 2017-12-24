package org.pcsoft.plugins.intellij.iss.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssPreprocessorElement;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssPreprocessorName;
import org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssPreprocessorSpecialValueCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getPreprocessorElementPattern().accepts(element)) {
            handlePreprocessorValue(completionResultSet, element);
        }
    }

    private void handlePreprocessorValue(@NotNull CompletionResultSet completionResultSet, PsiElement element) {
        IssPreprocessorElement preprocessorElement = PsiTreeUtil.getParentOfType(element, IssPreprocessorElement.class);
        if (preprocessorElement == null) {
            //Search backward for preprocessor
            PsiElement currentElement = element.getPrevSibling();
            while (currentElement != null && !(currentElement instanceof IssPreprocessorElement)) {
                currentElement = currentElement.getPrevSibling();
            }
            if (currentElement == null)
                return;
            preprocessorElement = (IssPreprocessorElement) currentElement;
        }
        PreprocessorType preprocessorType = preprocessorElement.getPreprocessorType();
        if (preprocessorType == null)
            return;
        Class<? extends SpecialValueType> specialValueTypeClass = preprocessorType.getSpecialValueTypeClass();
        if (specialValueTypeClass == null)
            return;

        for (final SpecialValueType specialValueType : specialValueTypeClass.getEnumConstants()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(specialValueType.getName())
                            .withItemTextForeground(IssHighlighting.LABEL.getDefaultAttributes().getForegroundColor())
                            .withBoldness(IssHighlighting.LABEL.getDefaultAttributes().getFontType() == Font.BOLD)
                            .withTypeText(preprocessorType.getName())
                            .withStrikeoutness(specialValueType.isDeprecated())
            );
        }
    }

    @NotNull
    private ElementPattern<PsiElement> getPreprocessorElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement(IssGenTypes.NAME)
                                .afterLeaf(
                                        PlatformPatterns.psiElement()
                                                .withText("#")
                                                .withParent(IssPreprocessorName.class)
                                )
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
}
