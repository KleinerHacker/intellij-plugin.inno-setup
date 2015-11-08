package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonUserOrGroupIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryPermissionsCompletionContributor extends CompletionContributor {
    public IssDirectoryPermissionsCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssDirectoryPropertyPermissionsElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssFlag userOrGroupIdentifier : IssCommonUserOrGroupIdentifier.values()) {
                            for (final IssFlag permission : IssCommonIOPermissions.values()) {
                                completionResultSet.addElement(LookupElementBuilder.create(userOrGroupIdentifier.getId() + "-" + permission.getId())
                                        .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(new JBColor(0x008000, 0x008000))
                                        .withInsertHandler((insertionContext, lookupElement) -> {
                                            insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                        }));
                            }
                        }
                    }
                });
    }
}