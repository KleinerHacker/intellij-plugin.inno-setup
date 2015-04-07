package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentDefinitionNameValueElement extends IssAbstractElement {

    public IssComponentDefinitionNameValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        return super.setName(s);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    public IssComponentDefinitionNameElement getValueParent() {
        return PsiTreeUtil.getParentOfType(this, IssComponentDefinitionNameElement.class);
    }
}
