package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssTaskReference;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFileUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyTasksValueElement extends IssDefinitionPropertyValueElement {

    public IssFilePropertyTasksValueElement(ASTNode node) {
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
        final PsiElement nameElement = IssFileUtils.createTaskReference(getProject(), s);
        final ASTNode childNode = nameElement.getNode().getFirstChildNode();

        getNode().replaceChild(getNode().getFirstChildNode(), childNode);

        return childNode.getPsi();
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new IssTaskReference(this, true);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
