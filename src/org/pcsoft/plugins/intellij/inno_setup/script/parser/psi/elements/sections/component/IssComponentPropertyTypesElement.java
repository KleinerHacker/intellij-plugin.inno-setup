package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssComponentPropertyTypesElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement,IssComponentPropertyTypesValueElement, IssComponentProperty> {
    public IssComponentPropertyTypesElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class,IssComponentPropertyTypesValueElement.class);
    }

    @NotNull
    @Override
    public IssComponentProperty getPropertyType() {
        return IssComponentProperty.Types;
    }
}
