package org.pcsoft.plugins.intellij.iss.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssSectionCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getSectionTitleElementPattern().accepts(element)) {
            handleSectionTitle(completionResultSet);
        }
    }

    private void handleSectionTitle(@NotNull CompletionResultSet completionResultSet) {
        for (final SectionType sectionType : SectionType.values()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(sectionType.getName())
                            .withStrikeoutness(sectionType.isDeprecated())
                            .withBoldness(sectionType.isRequired())
                            .withIcon(sectionType.getIcon())
            );
        }
    }


    @NotNull
    private ElementPattern<PsiElement> getSectionTitleElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.psiElement(IssCustomTypes.BRACES_CORNER_OPEN)
                                .withParent(IssSectionTitle.class)
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
}
