package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssComponentReference;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTaskUtils;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssTaskPropertyComponentsValueElement extends IssDefinitionPropertyValueElement {

    public IssTaskPropertyComponentsValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        final PsiElement nameElement = IssTaskUtils.createComponentReference(getProject(), s);
        final ASTNode childNode = nameElement.getNode().getFirstChildNode();

        getNode().replaceChild(getNode().getFirstChildNode(), childNode);

        return childNode.getPsi();
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new IssComponentReference(this, true);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
