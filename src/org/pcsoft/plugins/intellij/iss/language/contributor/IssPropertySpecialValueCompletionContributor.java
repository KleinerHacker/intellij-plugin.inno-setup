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
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssDefaultProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssPropertySpecialValueCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getPropertySpecialValueElementPattern().accepts(element)) {
            handlePropertySpecialValue(completionResultSet, element);
        }
    }

    //<editor-fold desc="Handler">
    private void handlePropertySpecialValue(@NotNull CompletionResultSet completionResultSet, PsiElement element) {
        IssProperty property = PsiTreeUtil.getParentOfType(element, IssProperty.class);
        if (property == null) {
            //Search backward for property
            PsiElement currentElement = element.getPrevSibling();
            while (currentElement != null && !(currentElement instanceof IssProperty)) {
                currentElement = currentElement.getPrevSibling();
            }
            if (currentElement == null)
                return;
            property = (IssProperty) currentElement;
        }
        final PropertyType propertyType = property.getPropertyType();
        if (propertyType == null || propertyType.getSpecialValueTypeClass() == null)
            return;

        for (final SpecialValueType specialValueType : propertyType.getSpecialValueTypeClass().getEnumConstants()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(specialValueType.getName())
                            .withItemTextForeground(IssHighlighting.LABEL.getDefaultAttributes().getForegroundColor())
                            .withBoldness(IssHighlighting.LABEL.getDefaultAttributes().getFontType() == Font.BOLD)
                            .withTypeText(propertyType.getName())
                            .withStrikeoutness(specialValueType.isDeprecated())
            );
        }
    }

    @NotNull
    private ElementPattern<PsiElement> getPropertySpecialValueElementPattern() {
        return PlatformPatterns.or(
                PlatformPatterns.psiElement()
                        .afterLeaf(PlatformPatterns.or(
                                PlatformPatterns.psiElement()
                                        .withText(":")
                                        .afterLeaf(PlatformPatterns.psiElement(IssGenTypes.NAME))
                                        .withParent(IssMultipleProperty.class),
                                PlatformPatterns.psiElement()
                                        .withText("=")
                                        .afterLeaf(PlatformPatterns.psiElement(IssGenTypes.NAME))
                                        .withParent(IssDefaultProperty.class)
                        ))
                        .withLanguage(IssLanguage.INSTANCE),
                PlatformPatterns.psiElement()
                        .afterLeaf(
                                PlatformPatterns.psiElement(IssGenTypes.NAME)
                                        .withSuperParent(2, IssValue.class)
                        )
                        .withLanguage(IssLanguage.INSTANCE)
        );
    }
}
