package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentDefinitionDescriptionElement extends IssDefinitionItemElement<IssComponentDefinitionElement> {

    public IssComponentDefinitionDescriptionElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class);
    }

    @Nullable
    public IssComponentDefinitionDescriptionValueElement getDescriptionValue() {
        return PsiTreeUtil.findChildOfType(this, IssComponentDefinitionDescriptionValueElement.class);
    }
}
