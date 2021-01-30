package org.pcsoft.plugins.intellij.iss.core.language.reference;

import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

public abstract class IssAbstractReference extends PsiReferenceBase<PsiNamedElement> implements PsiPolyVariantReference {
    protected final String key;
    protected final boolean strictSearch;

    protected IssAbstractReference(PsiNamedElement element, boolean strictSearch) {
        super(element);
        this.key = element.getName();
        this.strictSearch = strictSearch;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        final ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
