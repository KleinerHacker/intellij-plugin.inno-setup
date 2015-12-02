package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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
        final Collection<IssTaskDefinitionElement> taskDefinitionElements = IssFindUtils.findTaskDefinitionElements(myElement.getProject(), key, b);
        final List<IssPropertyNameValueElement> nameValueElements = taskDefinitionElements.stream()
                .filter(item -> item.getTaskName() != null && item.getTaskName().getPropertyValue() != null)
                .map(item -> item.getTaskName().getPropertyValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssTaskDefinitionElement> taskDefinitionElements = IssFindUtils.findTaskDefinitionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = taskDefinitionElements.stream()
                .filter(item -> item.getName() != null && !item.getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item)
                        .withPresentableText(item.getName())
                        .withTailText(item.getTaskDescription() != null && item.getTaskDescription().getPropertyValue() != null ?
                                " \"" + item.getTaskDescription().getPropertyValue().getString() + "\"" : null)
                        .withTypeText(item.getTaskGroupDescription() != null && item.getTaskGroupDescription().getPropertyValue() != null ?
                                "\"" + item.getTaskGroupDescription().getPropertyValue().getString() + "\"" : null)
                        .withIcon(IssSectionType.Task.getIcon()))
                .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssPropertyNameValueElement &&
                PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameElement = (IssPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
