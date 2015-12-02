package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import javax.swing.Icon;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractValueCompletionContributor<E extends IssPropertyElement> extends CompletionContributor {
    protected IssAbstractValueCompletionContributor(final Class<E> elementClass) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssPropertyValue item : getFlagList()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.GREEN)
                                    .withIcon(getIcon(item)).withStrikeoutness(item.isDeprecated()).withTailText(getTailText(item))
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }

    @NotNull
    protected abstract IssPropertyValue[] getFlagList();

    @Nullable
    protected Icon getIcon(IssPropertyValue propertyValue) {
        return null;
    }

    @Nullable
    protected String getTailText(IssPropertyValue propertyValue) {
        return null;
    }
}
