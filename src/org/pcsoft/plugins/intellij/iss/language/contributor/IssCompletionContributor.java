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
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultiElement;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSingleElement;
import org.pcsoft.plugins.intellij.iss.language.type.Section;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssCompletionContributor extends CompletionContributor {
    public IssCompletionContributor() {
        buildSectionExtension();

        //Section values for each section
        for (final Section section : Section.values()) {
            final Class<? extends SectionValue> sectionValueClass = section.getSectionValueClass();
            final SectionValue[] sectionValueList = sectionValueClass.getEnumConstants();
            buildSectionValueExtension(section, sectionValueList);

            //Section value types for each section value with type enumeration
            for (SectionValue sectionValue : sectionValueList) {
                final Class<? extends SectionValueType> sectionValueTypeClass = sectionValue.getSectionValueTypeClass();
                if (sectionValueTypeClass == null)
                    continue;
                final SectionValueType[] sectionValueTypeList = sectionValueTypeClass.getEnumConstants();
                buildSectionValueTypeExtension(sectionValue, sectionValueTypeList);
            }
        }
    }

    private void buildSectionValueTypeExtension(SectionValue sectionValue, SectionValueType[] sectionValueTypeList) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(
                                PlatformPatterns.or(
                                        PlatformPatterns.psiElement()
                                                .withText(":")
                                                .afterLeaf(sectionValue.getName())
                                                .withParent(IssMultiElement.class),
                                        PlatformPatterns.psiElement()
                                                .withText("=")
                                                .afterLeaf(sectionValue.getName())
                                                .withParent(IssSingleElement.class)
                                )
                        )
                        .withLanguage(IssLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final SectionValueType sectionValueType : sectionValueTypeList) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(sectionValueType.getName())
                                            .withItemTextForeground(IssHighlighting.LABEL.getDefaultAttributes().getForegroundColor())
                                            .withBoldness(IssHighlighting.LABEL.getDefaultAttributes().getFontType() == Font.BOLD)
                                            .withTypeText(sectionValue.getName())
                            );
                        }
                    }
                }
        );
    }

    private void buildSectionValueExtension(Section section, SectionValue[] sectionValueList) {
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
                        for (final SectionValue sectionValue : sectionValueList) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(sectionValue.getName())
                                            .withItemTextForeground(IssHighlighting.KEYWORD.getDefaultAttributes().getForegroundColor())
                                            .withBoldness(IssHighlighting.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                                            .withTypeText(section.getName())
                                            .withIcon(section.getIcon())
                            );
                        }
                    }
                }
        );
    }

    private void buildSectionExtension() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(IssGenTypes.NAME)
                        .afterLeaf(
                                PlatformPatterns.psiElement(IssCustomTypes.BRACES_CORNER_OPEN)
                                        .withParent(IssSectionTitle.class)
                        )
                        .withLanguage(IssLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final Section section : Section.values()) {
                            completionResultSet.addElement(
                                    LookupElementBuilder.create(section.getName())
                                            .withIcon(section.getIcon())
                            );
                        }
                    }
                }
        );
    }
}
