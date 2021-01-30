package org.pcsoft.plugins.intellij.iss.core.language.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.IssLanguage;
import org.pcsoft.plugins.intellij.iss.core.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.core.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionTypeVariant;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

import java.awt.*;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssPropertyKeyCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        final PsiElement element = parameters.getOriginalPosition() != null ? parameters.getOriginalPosition() : parameters.getPosition();

        if (getPropertyKeyElementPattern().accepts(element)) {
            handlePropertyKey(completionResultSet, element);
        }
    }

    private void handlePropertyKey(@NotNull CompletionResultSet completionResultSet, PsiElement element) {
        IssSection section = PsiTreeUtil.getParentOfType(element, IssSection.class);
        if (section == null) {
            //Search backward for section
            PsiElement currentElement = element.getPrevSibling();
            while (currentElement != null && !(currentElement instanceof IssSection)) {
                currentElement = currentElement.getPrevSibling();
            }
            if (currentElement == null)
                return;
            section = (IssSection) currentElement;
        }
        final SectionType sectionType = section.getSectionType();
        if (sectionType == null)
            return;
        final Class<? extends PropertyType> sectionPropertyClass = sectionType.getSectionPropertyClass();

        for (final PropertyType propertyType : sectionPropertyClass.getEnumConstants()) {
            completionResultSet.addElement(
                    LookupElementBuilder.create(propertyType.getName())
                            .withItemTextForeground(IssHighlighting.KEYWORD.getDefaultAttributes().getForegroundColor())
                            .withBoldness(IssHighlighting.KEYWORD.getDefaultAttributes().getFontType() == Font.BOLD)
                            .withTypeText(sectionType.getName())
                            .withBoldness(propertyType.isRequired())
                            .withStrikeoutness(propertyType.isDeprecated())
                            .withInsertHandler((insertionContext, lookupElement) -> {
                                if (sectionType.getVariant() == SectionTypeVariant.LineBased) {
                                    insertionContext.getDocument().insertString(insertionContext.getTailOffset(), ": ");
                                    insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                } else if (sectionType.getVariant() == SectionTypeVariant.Default) {
                                    insertionContext.getDocument().insertString(insertionContext.getTailOffset(), "=");
                                    insertionContext.getEditor().getCaretModel().moveToOffset(insertionContext.getTailOffset());
                                }
                            })
            );
        }
    }

    @NotNull
    private ElementPattern<PsiElement> getPropertyKeyElementPattern() {
        return PlatformPatterns.psiElement()
                .afterLeaf(
                        PlatformPatterns.or(
                                PlatformPatterns.psiElement(IssGenTypes.EOL),
                                PlatformPatterns.psiElement()
                                        .withText(";")
                        )
                )
                .withLanguage(IssLanguage.INSTANCE);
    }
}
