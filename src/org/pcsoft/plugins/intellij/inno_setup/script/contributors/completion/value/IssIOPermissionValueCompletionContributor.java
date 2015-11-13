package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonUserOrGroupIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOPermissionValueCompletionContributor extends CompletionContributor {
    public IssIOPermissionValueCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssPropertyIOPermissionsElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssFlag userOrGroupIdentifier : IssCommonUserOrGroupIdentifier.values()) {
                            for (final IssFlag permission : IssCommonIOPermissions.values()) {
                                completionResultSet.addElement(LookupElementBuilder.create(userOrGroupIdentifier.getId() + "-" + permission.getId())
                                        .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.GREEN)
                                        .withIcon(IssIcons.IC_INFO_PERMISSION).withStrikeoutness(permission.isDeprecated())
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
