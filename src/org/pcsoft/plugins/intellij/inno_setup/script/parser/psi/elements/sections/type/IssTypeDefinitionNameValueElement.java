package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssTypeDefinitionNameValueElement extends IssAbstractElement{

    public IssTypeDefinitionNameValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
//        final PsiElement nameElement = IssTaskUtils.createNameElement(getProject(), s);
//        final ASTNode childNode = nameElement.getNode().getFirstChildNode();
//
//        getNode().replaceChild(getNode().getFirstChildNode(), childNode);
//
//        return childNode.getPsi();
        return super.setName(s);
    }

    @NotNull
    @Override
    public String getName() {
        return this.getText();
    }

    @Nullable
    public IssTypeDefinitionNameElement getValueParent() {
        return PsiTreeUtil.getParentOfType(this, IssTypeDefinitionNameElement.class);
    }
}
