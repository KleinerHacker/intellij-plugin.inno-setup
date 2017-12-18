package org.pcsoft.plugins.intellij.iss.language.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;
import org.pcsoft.plugins.intellij.iss.language.type.Section;
import org.pcsoft.plugins.intellij.iss.language.type.SectionValue;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssCompletionContributor extends CompletionContributor {
    public IssCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(IssCustomTypes.BRACES_CORNER_OPEN).withParent(IssSectionTitle.class)).withLanguage(IssLanguage.INSTANCE),
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

        for (final Section section : Section.values()) {
            final Class<? extends SectionValue> sectionValueClass = section.getSectionValueClass();
            final SectionValue[] enumList = sectionValueClass.getEnumConstants();

            extend(CompletionType.BASIC,
                    PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE),
                    new CompletionProvider<CompletionParameters>() {
                        @Override
                        protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                            for (final SectionValue sectionValue : enumList) {
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
    }
}
