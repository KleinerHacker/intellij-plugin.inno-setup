package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyDescriptionElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement, IssComponentPropertyDescriptionValueElement, IssComponentProperty> {
    public IssComponentPropertyDescriptionElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class, IssComponentPropertyDescriptionValueElement.class);
    }

    @NotNull
    @Override
    public IssComponentProperty getPropertyType() {
        return IssComponentProperty.Description;
    }
}
