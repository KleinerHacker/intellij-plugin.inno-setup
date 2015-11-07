package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssComponentUtils;

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
        final Collection<IssComponentDefinitionElement> componentDefinitionElements = IssComponentUtils.findComponentDefinitions(myElement.getProject(), key, b);
        final List<IssComponentPropertyNameValueElement> componentDefinitionNameValueElements = componentDefinitionElements.stream()
                .filter(item -> item.getComponentName() != null && item.getComponentName().getNameValue() != null)
                .map(item -> item.getComponentName().getNameValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = componentDefinitionNameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssComponentDefinitionElement> componentDefinitionNameValueElements = IssComponentUtils.findComponentDefinitions(myElement.getProject());
        final List<LookupElement> lookupElementList = componentDefinitionNameValueElements.stream().map(LookupElementBuilder::create).collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssComponentPropertyNameValueElement) {
            final IssComponentPropertyNameValueElement nameElement = (IssComponentPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
