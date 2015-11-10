package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyDescriptionElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement,IssTaskPropertyDescriptionValueElement,IssTaskProperty> {
    public IssTaskPropertyDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class,IssTaskPropertyDescriptionValueElement.class);
    }

    @NotNull
    @Override
    public IssTaskProperty getPropertyType() {
        return IssTaskProperty.Description;
    }
}
