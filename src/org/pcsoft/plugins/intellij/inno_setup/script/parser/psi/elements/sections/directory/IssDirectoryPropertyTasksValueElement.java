package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssTaskReference;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssDirectoryUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryPropertyTasksValueElement extends IssDefinitionPropertyValueElement {

    public IssDirectoryPropertyTasksValueElement(ASTNode node) {
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
        final PsiElement nameElement = IssDirectoryUtils.createTaskReference(getProject(), s);
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
