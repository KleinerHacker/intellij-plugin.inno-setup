package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssLanguageReference;

/**
 * Created by Christoph on 30.11.2015.
 */
public class IssIdentifierReferenceElement extends IssAbstractElement implements PsiNameIdentifierOwner {

    public IssIdentifierReferenceElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiReference getReference() {
        if (PsiTreeUtil.getParentOfType(this, IssMessageSectionElement.class) != null ||
                PsiTreeUtil.getParentOfType(this, IssCustomMessageSectionElement.class) != null)
            return new IssLanguageReference(this, true);

        return null;
    }
}
