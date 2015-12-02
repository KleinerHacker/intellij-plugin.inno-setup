package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIdentifierElement extends IssAbstractElement {

    public IssIdentifierElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getIdentifierName() == null ? "<UNKNOWN>" : getIdentifierName().getName();
    }

    @Nullable
    public IssIdentifierNameElement getIdentifierName() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierNameElement.class);
    }

    @Nullable
    public IssIdentifierReferenceElement getIdentifierReference() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierReferenceElement.class);
    }
}
