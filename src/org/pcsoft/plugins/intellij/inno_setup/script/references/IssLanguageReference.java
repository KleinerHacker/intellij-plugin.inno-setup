package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssLanguageDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 18.12.2014.
 */
public class IssLanguageReference extends IssAbstractReference {

    public IssLanguageReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssLanguageDefinitionElement> languageDefinitionElements = IssFindUtils.findLanguageDefinitionElements(myElement.getProject(), key, b);
        final List<IssPropertyNameValueElement> nameValueElements = languageDefinitionElements.stream()
                .filter(item -> item.getLanguageName() != null && item.getLanguageName().getPropertyValue() != null)
                .map(item -> item.getLanguageName().getPropertyValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssLanguageDefinitionElement> languageDefinitionElements = IssFindUtils.findLanguageDefinitionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = languageDefinitionElements.stream()
                .filter(item -> item.getName() != null && !item.getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item)
                        .withPresentableText(item.getName())
                        .withTailText(item.getLanguageMessageFile() != null && item.getLanguageMessageFile().getPropertyValue() != null ?
                                " (" + item.getLanguageMessageFile().getPropertyValue().getString() + ")" : null)
                        .withIcon(item.getLanguageType() != null ? item.getLanguageType().getFlagIcon() : null))
        .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssPropertyNameValueElement &&
                PsiTreeUtil.getParentOfType(element, IssLanguageDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameElement = (IssPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
