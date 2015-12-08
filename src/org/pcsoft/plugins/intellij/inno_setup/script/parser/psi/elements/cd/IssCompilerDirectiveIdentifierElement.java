package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveIdentifierElement extends IssAbstractElement implements PsiNamedElement {

    public IssCompilerDirectiveIdentifierElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    public IssCompilerDirectiveSectionElement getCompilerDirectiveSection() {
        return PsiTreeUtil.getParentOfType(this, IssCompilerDirectiveSectionElement.class);
    }
}
