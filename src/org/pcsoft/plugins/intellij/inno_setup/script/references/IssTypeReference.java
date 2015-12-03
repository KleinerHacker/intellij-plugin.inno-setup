package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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
        final Collection<IssTypeDefinitionElement> typeDefinitionElements = IssFindUtils.findTypeDefinitionElements(myElement.getProject(), key, b);
        final List<IssPropertyNameValueElement> nameValueElements = typeDefinitionElements.stream()
                .filter(item -> item.getTypeName() != null && item.getTypeName().getPropertyValue() != null)
                .map(item -> item.getTypeName().getPropertyValue())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssTypeDefinitionElement> typeDefinitionElements = IssFindUtils.findTypeDefinitionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = typeDefinitionElements.stream()
                .filter(item -> item.getName() != null && !item.getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item.getName())
                                .withCaseSensitivity(false)
                                .withTailText(item.getTypeDescription() != null && item.getTypeDescription().getPropertyValue() != null ?
                                        " \"" + item.getTypeDescription().getPropertyValue().getString() + "\"" : null)
                                .withIcon(IssSectionType.Type.getIcon())
                )
                .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssPropertyNameValueElement &&
                PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameElement = (IssPropertyNameValueElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
