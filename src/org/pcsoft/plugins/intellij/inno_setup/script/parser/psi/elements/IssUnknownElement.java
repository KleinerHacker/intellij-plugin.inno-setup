package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 14.12.2014.
 */
public class IssUnknownElement extends IssAbstractElement {

    public IssUnknownElement(ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        return null;
    }
}
