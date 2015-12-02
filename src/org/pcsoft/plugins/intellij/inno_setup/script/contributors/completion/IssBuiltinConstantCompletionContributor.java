package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssBuiltinConstant;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssBuiltinConstantCompletionContributor extends CompletionContributor {
    public IssBuiltinConstantCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssBuiltinConstantElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final IssPropertyValue item : IssBuiltinConstant.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(true).withCaseSensitivity(false).withItemTextForeground(JBColor.GREEN)
                                    .withIcon(null /* TODO */).withStrikeoutness(item.isDeprecated()).withTypeText(null /* TODO */)
                            );
                        }
                        completionResultSet.addElement(LookupElementBuilder.create("#")
                                .withBoldness(true).withItemTextForeground(JBColor.BLUE.brighter())
                                .withTypeText("Compiler Directive Constant")
                        );
                    }
                });
    }
}
