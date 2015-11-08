package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInternalConstants;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssInternalConstantCompletionContributor extends CompletionContributor {
    public IssInternalConstantCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssDefinitionPropertyValueElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssFlag internalConstants : IssInternalConstants.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create("{" + internalConstants.getId() + "}")
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.PINK)
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }
}
