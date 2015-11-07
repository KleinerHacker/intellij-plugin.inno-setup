package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyDescriptionElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement> {

    public IssTaskPropertyDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }

    @Nullable
    public IssTaskPropertyDescriptionValueElement getDescriptionValue() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyDescriptionValueElement.class);
    }
}
