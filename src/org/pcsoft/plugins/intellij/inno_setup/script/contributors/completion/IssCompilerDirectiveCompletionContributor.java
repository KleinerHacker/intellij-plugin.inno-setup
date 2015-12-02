package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionType;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssCompilerDirectiveCompletionContributor extends CompletionContributor {
    public IssCompilerDirectiveCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.and(
                        PlatformPatterns.not(PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssConstantNameElement.class)),
                        PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).afterLeaf("#")
                ),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssCompilerDirectiveSectionIdentifier item : IssCompilerDirectiveSectionType.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                            .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.BLUE.brighter())
                                            .withIcon(null /* TODO */)
                            );
                        }
                    }
                });
    }
}
