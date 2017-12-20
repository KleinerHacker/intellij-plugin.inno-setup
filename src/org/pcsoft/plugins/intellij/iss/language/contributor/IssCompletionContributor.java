package org.pcsoft.plugins.intellij.iss.language.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssDefaultProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleProperty;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssCompletionContributor extends CompletionContributor {
    public IssCompletionContributor() {
        buildSectionExtension();

        //Section values for each section
        for (final SectionType sectionType : SectionType.values()) {
            final Class<? extends SectionProperty> sectionValueClass = sectionType.getSectionValueClass();
            final SectionProperty[] sectionPropertyList = sectionValueClass.getEnumConstants();
            buildSectionValueExtension(sectionType, sectionPropertyList);

            //Section value types for each section value with type enumeration
            for (SectionProperty sectionProperty : sectionPropertyList) {
                final Class<? extends PropertySpecialValueType> sectionValueTypeClass = sectionProperty.getSectionValueTypeClass();
                if (sectionValueTypeClass == null)
                    continue;
                final PropertySpecialValueType[] propertySpecialValueTypeList = sectionValueTypeClass.getEnumConstants();
                buildSectionValueTypeExtension(sectionProperty, propertySpecialValueTypeList);
            }
        }
    }

    private void buildSectionValueTypeExtension(SectionProperty sectionProperty, PropertySpecialValueType[] propertySpecialValueTypeList) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(
                                PlatformPatterns.or(
                                        PlatformPatterns.psiElement()
                                                .withText(":")
                                                .afterLeaf(sectionProperty.getName())
                                                .withParent(IssMultipleProperty.class),
                                        PlatformPatterns.psiElement()
                                                .withText("=")
                                                .afterLeaf(sectionProperty.getName())
                                                .withParent(IssDefaultProperty.class)
                                )
                        )
                        .withLanguage(IssLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final PropertySpecialValueType propertySpecialValueType : propertySpecialValueTypeList) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(propertySpecialValueType.getName())
                                            .withItemTextForeground(IssHighlighting.LABEL.getDefaultAttributes().getForegroundColor())
                                            .withBoldness(IssHighlighting.LABEL.getDefaultAttributes().getFontType() == Font.BOLD)
                                            .withTypeText(sectionProperty.getName())
                            );
                        }
                    }
                }
        );
    }

    private void buildSectionValueExtension(SectionType sectionType, SectionProperty[] sectionPropertyList) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(
                                PlatformPatterns.or(
                                        PlatformPatterns.psiElement(IssGenTypes.EOL),
                                        PlatformPatterns.psiElement()
                                                .withText(";")
                                )
                        )
                        .withLanguage(IssLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final SectionProperty sectionProperty : sectionPropertyList) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(sectionProperty.getName())
                                            .withItemTextForeground(IssHighlighting.KEYWORD.getDefaultAttributes().getForegroundColor())
                                            .withBoldness(IssHighlighting.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                            .withTypeText(sectionType.getName())
                                            .withIcon(sectionType.getIcon())
                            );
                        }
                    }
                }
        );
    }

    private void buildSectionExtension() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(
                                PlatformPatterns.psiElement(IssCustomTypes.BRACES_CORNER_OPEN)
                                        //.withParent(IssSectionTitle.class) TODO: Wrong element for pattern is used
                        )
                        .withLanguage(IssLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final SectionType sectionType : SectionType.values()) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(sectionType.getName())
                                            .withIcon(sectionType.getIcon())
                            );
                        }
                    }
                }
        );
    }
}
