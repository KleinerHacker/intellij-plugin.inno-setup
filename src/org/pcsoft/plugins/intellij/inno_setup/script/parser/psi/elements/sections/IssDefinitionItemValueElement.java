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
public abstract class IssDefinitionItemValueElement extends IssAbstractElement {

    public IssDefinitionItemValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssIdentifierElement getIdentifier() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierElement.class);
    }

    public abstract IssItemValueType getItemValueType();
}
