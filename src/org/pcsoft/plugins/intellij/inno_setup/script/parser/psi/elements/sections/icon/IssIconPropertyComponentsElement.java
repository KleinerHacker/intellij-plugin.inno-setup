package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssIconPropertyComponentsElement extends IssDefinitionPropertyElement<IssIconDefinitionElement,IssIconPropertyComponentsValueElement, IssIconProperty> {
    public IssIconPropertyComponentsElement(ASTNode node) {
        super(node, IssIconDefinitionElement.class,IssIconPropertyComponentsValueElement.class);
    }

    @NotNull
    @Override
    public IssIconProperty getPropertyType() {
        return IssIconProperty.Components;
    }
}
