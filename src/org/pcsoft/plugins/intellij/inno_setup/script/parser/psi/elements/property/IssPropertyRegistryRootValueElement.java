package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssRegistryRoot;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryRootValueElement extends IssPropertyValueElement implements PsiNamedElement {

    public IssPropertyRegistryRootValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    public IssRegistryRoot getRootType() {
        return IssRegistryRoot.fromId(getText());
    }
}
