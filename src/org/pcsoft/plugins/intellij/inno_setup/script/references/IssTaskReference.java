package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTaskUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
* Created by Christoph on 18.12.2014.
*/
public class IssTaskReference extends IssAbstractReference {

    public IssTaskReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssTaskDefinitionElement> taskDefinitionElements = IssTaskUtils.findTaskDefinitions(myElement.getProject(), key, b);
        final List<IssTaskPropertyNameValueElement> taskDefinitionNameValueElements = taskDefinitionElements.stream()
                .filter(item -> item.getTaskName() != null && item.getTaskName().getNameValue() != null)
                .map(item -> item.getTaskName().getNameValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = taskDefinitionNameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssTaskDefinitionElement> taskDefinitionElements = IssTaskUtils.findTaskDefinitions(myElement.getProject());
        final List<LookupElement> lookupElementList = taskDefinitionElements.stream().map(LookupElementBuilder::create).collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssTaskPropertyNameValueElement) {
            final IssTaskPropertyNameValueElement nameElement = (IssTaskPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
