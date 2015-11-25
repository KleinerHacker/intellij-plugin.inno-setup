package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
* Created by Christoph on 18.12.2014.
*/
public class IssComponentReference extends IssAbstractReference {

    public IssComponentReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssComponentDefinitionElement> componentDefinitionElements = IssFindUtils.findComponentDefinitionElements(myElement.getProject(), key, b);
        final List<IssPropertyNameValueElement> nameValueElements = componentDefinitionElements.stream()
                .filter(item -> item.getComponentName() != null && item.getComponentName().getPropertyValue() != null)
                .map(item -> item.getComponentName().getPropertyValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssComponentDefinitionElement> componentDefinitionNameValueElements = IssFindUtils.findComponentDefinitionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = componentDefinitionNameValueElements.stream()
                .filter(item -> item.getName() != null && !item.getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item).withPresentableText(item.getName()))
                .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssPropertyNameValueElement &&
                PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameElement = (IssPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
