package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssValueElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssDefinitionItemElement<P extends IssDefinitionElement> extends IssAbstractElement {

    private final Class<P> clazz;

    public IssDefinitionItemElement(ASTNode node, Class<P> clazz) {
        super(node);
        this.clazz = clazz;
    }

    @Nullable
    public IssIdentifierElement getIdentifier() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierElement.class);
    }

    @Nullable
    public IssValueElement getValue() {
        return PsiTreeUtil.findChildOfType(this, IssValueElement.class);
    }

    @Nullable
    public P getDefinition() {
        if (clazz == null)
            throw new IllegalStateException("Method call not supported");

        return PsiTreeUtil.getParentOfType(this, clazz);
    }
}
