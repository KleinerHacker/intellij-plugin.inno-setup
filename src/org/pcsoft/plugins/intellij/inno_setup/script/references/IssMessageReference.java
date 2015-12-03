package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssMessageConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 18.12.2014.
 */
public class IssMessageReference extends IssAbstractReference {

    public IssMessageReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssPropertyDefaultElement> messageDefinitionElements = IssFindUtils.findMessageDefinitionElements(myElement.getProject(), key, b);
        final List<IssIdentifierNameElement> nameValueElements = messageDefinitionElements.stream()
                .filter(item -> item.getIdentifier() != null && item.getIdentifier().getIdentifierName() != null)
                .map(item -> item.getIdentifier().getIdentifierName())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());
        
        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssPropertyDefaultElement> messageDefinitionElements = IssFindUtils.findMessageDefinitionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = messageDefinitionElements.stream()
                .filter(item -> item.getIdentifier() != null && item.getIdentifier().getIdentifierName() != null &&
                        !item.getIdentifier().getIdentifierName().getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item.getIdentifier().getIdentifierName().getName())
                        .withCaseSensitivity(false)
                        .withTypeText(item.getSection() == null || item.getSection().getSectionType() == null ? null :
                                item.getSection().getSectionType().getId())
                        .withIcon(IssSectionType.Message.getIcon())
                )
                .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssConstantNameElement &&
                PsiTreeUtil.getParentOfType(element, IssMessageConstantElement.class) != null) {
            final IssConstantNameElement nameElement = (IssConstantNameElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
