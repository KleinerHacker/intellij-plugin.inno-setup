package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.IssLookupElementHint;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

import javax.swing.Icon;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractPropertyCompletionContributor<E extends IssSectionElement> extends CompletionContributor implements IssLookupElementHint<IssPropertyIdentifier> {
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
                            completionResultSet.addElement(IssLanguageHighlightingColorFactory
                                    .buildLookupElement(getItemText(item), getTextAttributesKey(item))
                                    .withCaseSensitivity(false)
                                    .withTailText(getTailText(item))
                                    .withTypeText(getTypeText(item))
                                    .withIcon(getIcon(item))
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

    @Override
    @NotNull
    public TextAttributesKey getTextAttributesKey(IssPropertyIdentifier value) {
        if (value.isRequired())
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_REQUIRED;
        else if (value.isDeprecated())
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_DEPRECATED;
        else
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD;
    }

    @NotNull
    @Override
    public String getItemText(IssPropertyIdentifier value) {
        return value.getId();
    }

    @NotNull
    protected abstract IssPropertyIdentifier[] getSectionIdentifierList();

    @Override
    @Nullable
    public String getTypeText(IssPropertyIdentifier propertyIdentifier) {
        return null;
    }

    @Override
    @Nullable
    public String getTailText(IssPropertyIdentifier propertyIdentifier) {
        return propertyIdentifier.isRequired() ? " (Required)" : null;
    }

    @Nullable
    @Override
    public Icon getIcon(IssPropertyIdentifier value) {
        return null;
    }
}
