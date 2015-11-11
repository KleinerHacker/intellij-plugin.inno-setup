package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssTaskReference;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssIconPropertyTasksValueElement extends IssDefinitionPropertyValueElement {

    public IssIconPropertyTasksValueElement(ASTNode node) {
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

//    @Override
//    public PsiElement setName(String s) throws IncorrectOperationException {
//        final PsiElement nameElement = IssFileUtils.createTaskReference(getProject(), s);
//        final ASTNode childNode = nameElement.getNode().getFirstChildNode();
//
//        getNode().replaceChild(getNode().getFirstChildNode(), childNode);
//
//        return childNode.getPsi();
//    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new IssTaskReference(this, true);
    }
}
