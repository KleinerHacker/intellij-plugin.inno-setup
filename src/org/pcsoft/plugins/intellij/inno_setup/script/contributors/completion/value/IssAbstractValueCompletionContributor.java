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
import org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.IssLookupElementHint;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import javax.swing.Icon;
import java.awt.Color;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractValueCompletionContributor<E extends IssPropertyElement> extends CompletionContributor implements IssLookupElementHint<IssPropertyValue> {
    protected IssAbstractValueCompletionContributor(final Class<E> elementClass) {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssPropertyValue item : getFlagList()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(getBoldness(item)).withCaseSensitivity(false).withTypeText(getTypeText(item))
                                    .withItemTextForeground(getTextColor(item))
                                    .withIcon(getIcon(item)).withStrikeoutness(getStrikeout(item)).withTailText(getTailText(item))
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
    public boolean getBoldness(IssPropertyValue value) {
        return true;
    }

    @Override
    public boolean getStrikeout(IssPropertyValue value) {
        return value.isDeprecated();
    }

    @NotNull
    @Override
    public Color getTextColor(IssPropertyValue value) {
        return JBColor.foreground();
    }
}
