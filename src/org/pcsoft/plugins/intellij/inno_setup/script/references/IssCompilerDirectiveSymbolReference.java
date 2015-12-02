package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveIdentifierParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 18.12.2014.
 */
public class IssCompilerDirectiveSymbolReference extends IssAbstractReference {

    public IssCompilerDirectiveSymbolReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final Collection<IssCompilerDirectiveSymbolSectionElement> compilerDirectiveSectionElements = IssFindUtils.findCompilerDirectiveSymbolSectionElements(myElement.getProject(), key, b);
        final List<IssCompilerDirectiveIdentifierParameterElement> nameValueElements = compilerDirectiveSectionElements.stream()
                .filter(item -> item.getSymbolName() != null)
                .map(item -> item.getSymbolName())
                .collect(Collectors.toList());
        final List<ResolveResult> resolveResultList = nameValueElements.stream()
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Collection<IssCompilerDirectiveSymbolSectionElement> compilerDirectiveSectionElements = IssFindUtils.findCompilerDirectiveSymbolSectionElements(myElement.getProject());
        final List<LookupElement> lookupElementList = compilerDirectiveSectionElements.stream()
                .filter(item -> item.getSymbolName() != null && !item.getSymbolName().getName().trim().isEmpty())
                .map(item -> LookupElementBuilder.create(item.getSymbolName())
                        .withItemTextForeground(JBColor.BLUE.brighter())
                        .withPresentableText(item.getSymbolName().getName())
                        .withIcon(null /* TODO */))
                .collect(Collectors.toList());

        return lookupElementList.toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssCompilerDirectiveIdentifierParameterElement &&
                PsiTreeUtil.getParentOfType(element, IssCompilerDirectiveSymbolSectionElement.class) != null) {
            final IssCompilerDirectiveIdentifierParameterElement nameElement = (IssCompilerDirectiveIdentifierParameterElement) element;
            return nameElement.getName().equalsIgnoreCase(myElement.getName());
        }

        return false;
    }
}
