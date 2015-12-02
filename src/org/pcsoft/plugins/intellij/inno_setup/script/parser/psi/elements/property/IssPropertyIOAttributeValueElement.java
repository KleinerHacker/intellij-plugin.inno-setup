package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyIOAttributeValueElement extends IssPropertyValueElement implements PsiNamedElement {

    public IssPropertyIOAttributeValueElement(ASTNode node) {
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
