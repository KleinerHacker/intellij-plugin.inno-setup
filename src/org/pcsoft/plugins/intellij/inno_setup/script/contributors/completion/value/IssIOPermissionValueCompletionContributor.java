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
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOUserOrGroupIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOPermissionValueCompletionContributor extends CompletionContributor {
    public IssIOPermissionValueCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssPropertyIOPermissionsElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        for (final IssPropertyValue userOrGroupIdentifier : IssIOUserOrGroupIdentifier.values()) {
                            for (final IssPropertyValue permission : IssIOPermissions.values()) {
                                completionResultSet.addElement(IssLanguageHighlightingColorFactory
                                        .buildLookupElement(userOrGroupIdentifier.getId() + "-" + permission.getId(), getTextAttributesKey(userOrGroupIdentifier, permission))
                                        .withCaseSensitivity(false)
                                        .withIcon(IssIcons.IC_INFO_PERMISSION)
                                        .withInsertHandler((insertionContext, lookupElement) -> {
                                            insertionContext.getDocument().insertString(insertionContext.getTailOffset(), " ");
                                            insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                        }));
                            }
                        }
                    }
                });
    }

    @NotNull
    private TextAttributesKey getTextAttributesKey(IssPropertyValue value1, IssPropertyValue value2) {
        if (value1.isDeprecated() || value2.isDeprecated())
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_VALUE_DEPRECATED;
        else
            return IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_VALUE_STANDARD;
    }
}
