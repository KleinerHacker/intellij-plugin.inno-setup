package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.enums.EnumUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssFlagCompletionContributor<E extends IssDefinitionPropertyElement> extends CompletionContributor {
    protected IssFlagCompletionContributor(final Class<E> elementClass) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssFlag item : getFlagList()) {
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

    protected abstract IssFlag[] getFlagList();
}
