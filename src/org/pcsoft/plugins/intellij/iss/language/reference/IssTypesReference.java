package org.pcsoft.plugins.intellij.iss.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssRefValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.property.TypesPropertyType;
import org.pcsoft.plugins.intellij.iss.util.IssSearchForElementUtils;

import java.util.List;
import java.util.stream.Collectors;

public class IssTypesReference extends IssAbstractReference {
    public IssTypesReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final List<IssMultipleProperty> multipleProperties = IssSearchForElementUtils.searchForTypes(myElement.getProject(),
                (IssFile) myElement.getContainingFile(), key, b);
        final List<PsiElementResolveResult> resolveResults = multipleProperties.stream()
                .map(issMultipleProperty -> {
                    if (!issMultipleProperty.getMultipleValue().getRefValueList().isEmpty())
                        //Single value case for key
                        return issMultipleProperty.getMultipleValue().getRefValueList().get(0);
                    else
                        //String case for key
                        return issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().get(0);
                })
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResults.toArray(new ResolveResult[resolveResults.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final List<IssMultipleProperty> multipleProperties = IssSearchForElementUtils.searchForTypes(myElement.getProject(),
                (IssFile) myElement.getContainingFile());
        final List<LookupElementBuilder> lookupElements = multipleProperties.stream()
                .map(issMultipleProperty -> {
                            if (!issMultipleProperty.getMultipleValue().getRefValueList().isEmpty())
                                //Single value case for key
                                return LookupElementBuilder.create(issMultipleProperty.getMultipleValue().getRefValueList().get(0).getName())
                                        .withCaseSensitivity(false)
                                        .withTypeText(issMultipleProperty.getContainingFile().getName());
                            else
                                //String case for key
                                return LookupElementBuilder.create(issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().get(0).getName())
                                        .withCaseSensitivity(false)
                                        .withTypeText(issMultipleProperty.getContainingFile().getName());
                        }
                )
                .collect(Collectors.toList());

        return lookupElements.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssRefValue) {
            final IssProperty property = PsiTreeUtil.getParentOfType(element, IssProperty.class);
            if (property == null)
                return false;
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null || propertyType != TypesPropertyType.Name)
                return false;

            return ((IssRefValue) element).getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
