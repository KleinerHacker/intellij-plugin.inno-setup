package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTypeUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
* Created by Christoph on 18.12.2014.
*/
public class IssTypeReference extends IssAbstractReference {

    public IssTypeReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssTypeDefinitionElement> typeDefinitionElements = IssTypeUtils.findTypeDefinitions(myElement.getProject(), key, b);
        final List<IssTypeDefinitionNameValueElement> typeDefinitionNameValueElements = typeDefinitionElements.stream()
                .filter(item -> item.getTypeName() != null && item.getTypeName().getNameValue() != null)
                .map(item -> item.getTypeName().getNameValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = typeDefinitionNameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssTypeDefinitionElement> typeDefinitionElements = IssTypeUtils.findTypeDefinitions(myElement.getProject());
        final List<LookupElement> lookupElementList = typeDefinitionElements.stream().map(LookupElementBuilder::create).collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssTypeDefinitionNameValueElement) {
            final IssTypeDefinitionNameValueElement nameElement = (IssTypeDefinitionNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
