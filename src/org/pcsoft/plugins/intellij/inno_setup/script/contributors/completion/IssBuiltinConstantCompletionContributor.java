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
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

import java.awt.Font;

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
                        for (final IssBuiltinConstant item : IssBuiltinConstant.values()) {
                            completionResultSet.addElement(LookupElementBuilder.create(item.getId())
                                    .withBoldness(item.getType().getFontStyle() == Font.BOLD)
                                    .withCaseSensitivity(false)
                                    .withItemTextForeground(item.getType().getTextColor())
                                    .withIcon(null /* TODO */)
                                    .withStrikeoutness(item.isDeprecated()).withTypeText(item.getType().getText())
                            );
                        }
                        completionResultSet.addElement(LookupElementBuilder.create("#")
                                .withBoldness(false).withItemTextForeground(JBColor.BLUE.brighter())
                                .withTypeText("Compiler Directive Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("cm:")
                                .withBoldness(false).withItemTextForeground(JBColor.GREEN)
                                .withTypeText("Custom Message Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("%")
                                .withBoldness(false).withItemTextForeground(JBColor.YELLOW)
                                .withTypeText("Environment Variable")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("param:")
                                .withBoldness(false).withItemTextForeground(JBColor.YELLOW)
                                .withTypeText("Command Line Parameter")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("reg:")
                                .withBoldness(true).withItemTextForeground(JBColor.CYAN)
                                .withTypeText("Registry Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("ini:")
                                .withBoldness(true).withItemTextForeground(JBColor.CYAN)
                                .withTypeText("INI Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("drive:")
                                .withBoldness(true).withItemTextForeground(JBColor.YELLOW)
                                .withTypeText("Drive Constant")
                        );
                    }
                });
    }
}
