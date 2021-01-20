package org.pcsoft.plugins.intellij.iss.language.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType;
import org.pcsoft.plugins.intellij.iss.util.IssSearchForElementUtils;

import java.util.List;
import java.util.stream.Collectors;

public class IssDefineSymbolReference extends IssAbstractReference {
    public IssDefineSymbolReference(PsiNamedElement element, boolean strictSearch) {
        super(element, strictSearch);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        final List<IssPreprocessorElement> preprocessorElements = IssSearchForElementUtils.searchForDefineSymbols(myElement.getProject(),
                (IssFile) myElement.getContainingFile(), key, b);
        final List<PsiElementResolveResult> resolveResults = preprocessorElements.stream()
                .filter(preprocessorElement -> preprocessorElement.getType() == PreprocessorType.Define)
                .map(preprocessorElement -> preprocessorElement.getPreprocessorDefine().getPreprocessorName())
                .map(PsiElementResolveResult::new)
                .collect(Collectors.toList());

        return resolveResults.toArray(new ResolveResult[resolveResults.size()]);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final List<IssPreprocessorElement> preprocessorElements = IssSearchForElementUtils.searchForDefineSymbols(myElement.getProject(),
                (IssFile) myElement.getContainingFile());

        return preprocessorElements.stream()
                .filter(preprocessorElement -> preprocessorElement.getType() == PreprocessorType.Define)
                .map(preprocessorElement ->
                        LookupElementBuilder.create(preprocessorElement.getPreprocessorDefine().getPreprocessorName())
                                .withCaseSensitivity(false)
                                .withTailText(preprocessorElement.getContainingFile().getName())
                ).toArray();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof IssPreprocessorName) {
            final IssPreprocessorElement preprocessorElement = PsiTreeUtil.getParentOfType(element, IssPreprocessorElement.class);
            if (preprocessorElement != null) {
                final PreprocessorType preprocessorType = preprocessorElement.getType();
                if (preprocessorType != PreprocessorType.Define)
                    return false;

                return preprocessorElement.getPreprocessorDefine().getPreprocessorName().getText().equalsIgnoreCase(myElement.getName());
            }
        }

        return false;
    }
}
