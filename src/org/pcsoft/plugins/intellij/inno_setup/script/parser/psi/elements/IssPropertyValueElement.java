package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssPropertyValueElement extends IssAbstractElement {

    public IssPropertyValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public final IssIdentifierElement getIdentifier() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierElement.class);
    }
}
