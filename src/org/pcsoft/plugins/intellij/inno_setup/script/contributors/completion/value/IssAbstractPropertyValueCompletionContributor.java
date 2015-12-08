package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

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
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import javax.swing.Icon;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractPropertyValueCompletionContributor<E extends IssPropertyElement> extends CompletionContributor implements IssLookupElementHint<IssPropertyValue> {
    protected IssAbstractPropertyValueCompletionContributor(final Class<E> elementClass) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final IssPropertyValue item : getValueList()) {
                            completionResultSet.addElement(IssLanguageHighlightingColorFactory
                                    .buildLookupElement(getItemText(item), getTextAttributesKey(item))
                                    .withCaseSensitivity(false)
                                    .withTypeText(getTypeText(item))
                                    .withIcon(getIcon(item))
                                    .withTailText(getTailText(item))
                                    .withInsertHandler((insertionContext, lookupElement) -> {
                                        insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                        insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                    }));
                        }
                    }
                });
    }

    @NotNull
    protected abstract IssPropertyValue[] getValueList();

    @NotNull
    @Override
    public String getItemText(IssPropertyValue value) {
        return value.getId();
    }

    @Override
    @Nullable
    public Icon getIcon(IssPropertyValue propertyValue) {
        return null;
    }

    @Override
    @Nullable
    public String getTailText(IssPropertyValue propertyValue) {
        return null;
    }

    @Nullable
    @Override
    public String getTypeText(IssPropertyValue value) {
        return null;
    }

    @Override
    @NotNull
    public TextAttributesKey getTextAttributesKey(IssPropertyValue value) {
        if (value.isDeprecated())
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_VALUE_DEPRECATED;
        else
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_VALUE_STANDARD;
    }
}
