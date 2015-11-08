package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssComponentUtils;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyNameValueElement extends IssDefinitionPropertyValueElement {

    public IssComponentPropertyNameValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        final PsiElement nameElement = IssComponentUtils.createNameElement(getProject(), s);
        final ASTNode childNode = nameElement.getNode().getFirstChildNode();

        getNode().replaceChild(getNode().getFirstChildNode(), childNode);

        return childNode.getPsi();
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    public IssComponentPropertyNameElement getValueParent() {
        return PsiTreeUtil.getParentOfType(this, IssComponentPropertyNameElement.class);
    }
}
