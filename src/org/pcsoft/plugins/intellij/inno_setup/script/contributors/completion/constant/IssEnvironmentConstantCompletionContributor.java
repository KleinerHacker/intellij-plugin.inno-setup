package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.constant;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssEnvironmentConstantElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssEnvironmentConstantCompletionContributor extends CompletionContributor {
    public IssEnvironmentConstantCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).inside(IssEnvironmentConstantElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
                        for (final String key : System.getenv().keySet()) {
                            final String value = System.getenv(key);

                            completionResultSet.addElement(
                                    IssLanguageHighlightingColorFactory
                                            .buildLookupElement(key, IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_ENVIRONMENT)
                                            .withTypeText(value).withCaseSensitivity(false)
                            );
                        }
                    }
                });
    }
}
