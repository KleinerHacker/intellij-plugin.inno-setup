package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssCompilerDirectiveSymbolReference;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssMessageReference;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssConstantNameElement extends IssAbstractElement implements PsiNamedElement {

    public IssConstantNameElement(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiReference getReference() {
        if (PsiTreeUtil.getParentOfType(this, IssCompilerDirectiveConstantElement.class) != null)
            return new IssCompilerDirectiveSymbolReference(this, true);
        if (PsiTreeUtil.getParentOfType(this, IssMessageConstantElement.class) != null)
            return new IssMessageReference(this, true);

        return null;
    }

    @Nullable
    public IssConstantElement getConstant() {
        return PsiTreeUtil.getParentOfType(this, IssConstantElement.class);
    }
}
