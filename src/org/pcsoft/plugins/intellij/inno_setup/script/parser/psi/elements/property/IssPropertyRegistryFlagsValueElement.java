package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryFlagsValueElement extends IssPropertyValueElement implements PsiNamedElement {

    public IssPropertyRegistryFlagsValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}
