package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

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
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractPropertyCompletionContributor<E extends IssSectionElement> extends CompletionContributor {
    protected enum PropertyType {
        Standard,
        Definable
    }

    protected IssAbstractPropertyCompletionContributor(Class<E> elementClass, PropertyType type) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass).afterLeaf(";", "\n", "\r", "\r\n"),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssPropertyIdentifier item : getSectionIdentifierList()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.BLUE)
                                    .withStrikeoutness(item.isDeprecated()).withTailText(item.isRequired() ? " (Required)" : null)
                                    .withTypeText(getTypeText(item))
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        if (type == PropertyType.Definable) {
                                            insertionContext.getDocument().insertString(insertionContext.getTailOffset(), ": ");
                                            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                        } else if (type == PropertyType.Standard) {
                                            insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " = ");
                                            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                        } else
                                            throw new IllegalStateException("Unknown property type: " + type);
                                    }));
                        }
                    }
                });
    }

    @NotNull
    protected abstract IssPropertyIdentifier[] getSectionIdentifierList();

    @Nullable
    protected String getTypeText(IssPropertyIdentifier propertyIdentifier) {
        return null;
    }
}
