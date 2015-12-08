package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.cd;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.IssLookupElementHint;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveValueIdentifier;

import javax.swing.Icon;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssAbstractCompilerDirectiveValueCompletionContributor<E extends IssCompilerDirectiveParameterElement> extends CompletionContributor implements IssLookupElementHint<IssCompilerDirectiveValueIdentifier> {
    protected IssAbstractCompilerDirectiveValueCompletionContributor(final Class<E> elementClass) {
        extend(CompletionType.BASIC,
                getElementPattern(elementClass),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final IssCompilerDirectiveValueIdentifier item : getValueList()) {
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
    protected ElementPattern<? extends PsiElement> getElementPattern(final Class<E> elementClass) {
        return PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(elementClass);
    }

    @NotNull
    protected abstract IssCompilerDirectiveValueIdentifier[] getValueList();

    @NotNull
    @Override
    public String getItemText(IssCompilerDirectiveValueIdentifier value) {
        return value.getId();
    }

    @Override
    @Nullable
    public Icon getIcon(IssCompilerDirectiveValueIdentifier propertyValue) {
        return null;
    }

    @Override
    @Nullable
    public String getTailText(IssCompilerDirectiveValueIdentifier propertyValue) {
        return null;
    }

    @Nullable
    @Override
    public String getTypeText(IssCompilerDirectiveValueIdentifier value) {
        return null;
    }

    @Override
    @NotNull
    public TextAttributesKey getTextAttributesKey(IssCompilerDirectiveValueIdentifier value) {
        if (value.isDeprecated())
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_PARAMETER_DEPRECATED;
        else
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_PARAMETER_STANDARD;
    }
}
