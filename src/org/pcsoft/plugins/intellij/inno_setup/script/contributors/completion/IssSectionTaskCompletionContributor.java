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
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSectionTaskCompletionContributor extends CompletionContributor {
    public IssSectionTaskCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssTaskSectionElement.class).afterLeaf(";", "\n", "\r", "\r\n"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssTaskProperty item : IssTaskProperty.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.BLUE)
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), ": ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }
}
