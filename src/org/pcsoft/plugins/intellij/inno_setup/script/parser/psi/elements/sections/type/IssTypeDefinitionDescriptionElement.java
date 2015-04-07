package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeDefinitionDescriptionElement extends IssDefinitionItemElement<IssTaskDefinitionElement> {

    public IssTypeDefinitionDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }

    @Nullable
    public IssTypeDefinitionDescriptionValueElement getDescriptionValue() {
        return PsiTreeUtil.findChildOfType(this, IssTypeDefinitionDescriptionValueElement.class);
    }
}
