package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

import java.util.Collection;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssComponentDefinitionFlagsElement extends IssDefinitionItemElement<IssComponentDefinitionElement> {

    public IssComponentDefinitionFlagsElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class);
    }

    @NotNull
    public Collection<IssComponentDefinitionFlagsValueElement> getFlagsValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssComponentDefinitionFlagsValueElement.class);
    }
}
