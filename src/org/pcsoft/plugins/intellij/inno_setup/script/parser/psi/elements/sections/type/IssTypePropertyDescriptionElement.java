package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypePropertyDescriptionElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement> {

    public IssTypePropertyDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }

    @Nullable
    public IssTypePropertyDescriptionValueElement getDescriptionValue() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyDescriptionValueElement.class);
    }
}
