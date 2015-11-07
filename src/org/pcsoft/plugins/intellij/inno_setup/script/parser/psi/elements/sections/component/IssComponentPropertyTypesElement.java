package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

import java.util.Collection;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssComponentPropertyTypesElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement> {

    public IssComponentPropertyTypesElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class);
    }

    @NotNull
    public Collection<IssComponentPropertyTypesValueElement> getTypesValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssComponentPropertyTypesValueElement.class);
    }
}
