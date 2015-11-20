package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractPropertyCompletionContributor<E extends IssDefinableSectionElement> extends CompletionContributor {
    protected IssAbstractPropertyCompletionContributor(Class<E> elementClass) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass).afterLeaf(";", "\n", "\r", "\r\n"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssDefinablePropertyIdentifier item : getSectionIdentifierList()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.BLUE)
                                    .withStrikeoutness(item.isDeprecated()).withTailText(item.isRequired() ? " (Required)" : null)
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), ": ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }

    protected abstract IssDefinablePropertyIdentifier[] getSectionIdentifierList();
}
