package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.constant;

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
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

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
                            completionResultSet.addElement(
                                    IssLanguageHighlightingColorFactory
                                            .buildLookupElement(item.getId(), item.getType().getTextAttributesKey())
                                            .withCaseSensitivity(false)
                                            .withIcon(null /* TODO */)
                                            .withStrikeoutness(item.isDeprecated())
                                            .withTypeText(item.getType().getText())
                            );
                        }
                        completionResultSet.addElement(
                                IssLanguageHighlightingColorFactory
                                        .buildLookupElement("#", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_COMPILER_DIRECTIVE)
                                        .withCaseSensitivity(false)
                                        .withTypeText("Compiler Directive Constant")
                        );
                        completionResultSet.addElement(
                                IssLanguageHighlightingColorFactory
                                        .buildLookupElement("cm:", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_MESSAGE)
                                        .withCaseSensitivity(false)
                                        .withTypeText("Custom Message Constant")
                        );
                        completionResultSet.addElement(
                                IssLanguageHighlightingColorFactory
                                        .buildLookupElement("%", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_ENVIRONMENT)
                                        .withCaseSensitivity(false)
                                        .withTypeText("Environment Variable")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("param:")
                                .withCaseSensitivity(false)
                                .withBoldness(false)
                                .withItemTextForeground(JBColor.YELLOW)
                                .withTypeText("Command Line Parameter")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("reg:")
                                .withCaseSensitivity(false)
                                .withBoldness(true)
                                .withItemTextForeground(JBColor.CYAN)
                                .withTypeText("Registry Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("ini:")
                                .withCaseSensitivity(false)
                                .withBoldness(true)
                                .withItemTextForeground(JBColor.CYAN)
                                .withTypeText("INI Constant")
                        );
                        completionResultSet.addElement(LookupElementBuilder.create("drive:")
                                .withCaseSensitivity(false)
                                .withBoldness(true)
                                .withItemTextForeground(JBColor.YELLOW)
                                .withTypeText("Drive Constant")
                        );
                    }
                });
    }
}
