package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssCopyModeFileCompletionContributor extends CompletionContributor {
    public IssCopyModeFileCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssFilePropertyCopyModeElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssFileCopyMode item : IssFileCopyMode.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(new JBColor(0x008000, 0x008000))
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }
}
