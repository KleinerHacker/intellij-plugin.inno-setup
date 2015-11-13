package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssTaskReference;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssCreationUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssPropertyTaskReferenceValueElement extends IssPropertyValueElement {

    public IssPropertyTaskReferenceValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        final PsiElement nameElement = IssCreationUtils.createTaskReferenceValueElement(getProject(), s);
        final ASTNode childNode = nameElement.getNode().getFirstChildNode();

        getNode().replaceChild(getNode().getFirstChildNode(), childNode);

        return childNode.getPsi();
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new IssTaskReference(this, true);
    }
}
