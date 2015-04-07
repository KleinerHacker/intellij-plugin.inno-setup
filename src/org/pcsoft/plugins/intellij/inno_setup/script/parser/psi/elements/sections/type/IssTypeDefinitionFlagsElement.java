package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

import java.util.Collection;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypeDefinitionFlagsElement extends IssDefinitionItemElement<IssTypeDefinitionElement> {

    public IssTypeDefinitionFlagsElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class);
    }

    @NotNull
    public Collection<IssTypeDefinitionFlagsValueElement> getFlagsValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssTypeDefinitionFlagsValueElement.class);
    }
}
