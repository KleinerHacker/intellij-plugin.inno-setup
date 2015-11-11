package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssComponentReference;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssIconPropertyComponentsValueElement extends IssDefinitionPropertyValueElement {

    public IssIconPropertyComponentsValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

//    @Override
//    public PsiElement setName(String s) throws IncorrectOperationException {
//        final PsiElement nameElement = IssFileUtils.createComponentReference(getProject(), s);
//        final ASTNode childNode = nameElement.getNode().getFirstChildNode();
//
//        getNode().replaceChild(getNode().getFirstChildNode(), childNode);
//
//        return childNode.getPsi();
//    }

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
}
