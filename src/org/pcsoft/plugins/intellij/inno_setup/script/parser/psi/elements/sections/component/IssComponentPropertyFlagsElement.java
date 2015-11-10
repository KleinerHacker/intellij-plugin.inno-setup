package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssComponentPropertyFlagsElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement,IssComponentPropertyFlagsValueElement, IssComponentProperty> {
    public IssComponentPropertyFlagsElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class,IssComponentPropertyFlagsValueElement.class);
    }

    @NotNull
    @Override
    public IssComponentProperty getPropertyType() {
        return IssComponentProperty.Flags;
    }
}
