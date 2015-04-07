package org.pcsoft.plugins.intellij.inno_setup.script.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 18.12.2014.
 */
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
