package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssLanguageFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionHeaderCompletionContributor extends CompletionContributor {
    public IssSectionHeaderCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssScriptFile.class)
                        .afterLeaf("\r", "\n", "\r\n"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssSectionType item : IssSectionType.values()) {
                            if (item.getFileType() != null) {
                                if (item.getFileType().getPsiFileClass() != IssScriptFile.class)
                                    continue;
                            }

                            buildList(completionResultSet, item);
                        }
                    }
                });
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssLanguageFile.class)
                        .afterLeaf("\r", "\n", "\r\n"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssSectionType item : IssSectionType.values()) {
                            if (item.getFileType() != null) {
                                if (item.getFileType().getPsiFileClass() != IssLanguageFile.class)
                                    continue;
                            }

                            buildList(completionResultSet, item);
                        }
                    }
                });
    }

    private void buildList(CompletionResultSet completionResultSet, IssSectionType item) {
        completionResultSet.addElement(LookupElementBuilder.create("[" + item.getId() + "]")
                .withBoldness(true).withCaseSensitivity(false).withIcon(item.getIcon())
                .withInsertHandler((insertionContext, lookupElement) -> {
                    if (insertionContext.getDocument().getCharsSequence().charAt(insertionContext.getStartOffset()-1) == '[') {
                        insertionContext.getDocument().deleteString(insertionContext.getStartOffset()-1, insertionContext.getStartOffset());
                    }
                    insertionContext.getDocument().insertString(insertionContext.getTailOffset(), "\n");
                    insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                }));
    }
}
