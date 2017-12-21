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
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getSectionTitleElementPattern().accepts(element)) {
            handleSectionTitle(completionResultSet);
            return;
        }
        if (getPropertyKeyElementPattern().accepts(element)) {
            handlePropertyKey(completionResultSet, element);
            return;
        }
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
        if (propertyType == null || propertyType.getPropertySpecialValueTypeClass() == null)
            return;

        for (final PropertySpecialValueType propertySpecialValueType : propertyType.getPropertySpecialValueTypeClass().getEnumConstants()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(propertySpecialValueType.getName())
                            .withItemTextForeground(IssHighlighting.LABEL.getDefaultAttributes().getForegroundColor())
                            .withBoldness(IssHighlighting.LABEL.getDefaultAttributes().getFontType() == Font.BOLD)
                            .withTypeText(propertyType.getName())
                            .withStrikeoutness(propertySpecialValueType.isDeprecated())
            );
        }
    }

    private void handlePropertyKey(@NotNull CompletionResultSet completionResultSet, PsiElement element) {
        IssSection section = PsiTreeUtil.getParentOfType(element, IssSection.class);
        if (section == null) {
            //Search backward for section
            PsiElement currentElement = element.getPrevSibling();
            while (currentElement != null && !(currentElement instanceof IssSection)) {
                currentElement = currentElement.getPrevSibling();
            }
            if (currentElement == null)
                return;
            section = (IssSection) currentElement;
        }
        final SectionType sectionType = section.getSectionType();
        if (sectionType == null)
            return;
        final Class<? extends PropertyType> sectionPropertyClass = sectionType.getSectionPropertyClass();

        for (final PropertyType propertyType : sectionPropertyClass.getEnumConstants()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(propertyType.getName())
                            .withItemTextForeground(IssHighlighting.KEYWORD.getDefaultAttributes().getForegroundColor())
                            .withBoldness(IssHighlighting.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                            .withTypeText(sectionType.getName())
                            .withBoldness(propertyType.isRequired())
                            .withStrikeoutness(propertyType.isDeprecated())
                            .withIcon(sectionType.getIcon())
            );
        }
    }

    private void handleSectionTitle(@NotNull CompletionResultSet completionResultSet) {
        for (final SectionType sectionType : SectionType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(sectionType.getName())
                            .withStrikeoutness(sectionType.isDeprecated())
                            .withBoldness(sectionType.isRequired())
                            .withIcon(sectionType.getIcon())
            );
        }
    }
    //</editor-fold>

    //<editor-fold desc="Element Patterns">
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

    @NotNull
    private ElementPattern<PsiElement> getPropertyKeyElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.or(
                                PlatformPatterns.psiElement(IssGenTypes.EOL),
                                PlatformPatterns.psiElement()
                                        .withText(";")
                        )
                )
                .withLanguage(IssLanguage.INSTANCE);
    }

    @NotNull
    private ElementPattern<PsiElement> getSectionTitleElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement(IssCustomTypes.BRACES_CORNER_OPEN)
                                .withParent(IssSectionTitle.class)
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
    //</editor-fold>
}
