package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

import java.util.Collection;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTaskDefinitionFlagsElement extends IssDefinitionItemElement<IssTaskDefinitionElement> {

    public IssTaskDefinitionFlagsElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }

    @NotNull
    public Collection<IssTaskDefinitionFlagsValueElement> getFlagsValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssTaskDefinitionFlagsValueElement.class);
    }
}
