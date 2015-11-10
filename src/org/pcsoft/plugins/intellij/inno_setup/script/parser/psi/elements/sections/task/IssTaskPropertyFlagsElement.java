package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTaskPropertyFlagsElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement,IssTaskPropertyFlagsValueElement,IssTaskProperty> {
    public IssTaskPropertyFlagsElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class,IssTaskPropertyFlagsValueElement.class);
    }

    @NotNull
    @Override
    public IssTaskProperty getPropertyType() {
        return IssTaskProperty.Flags;
    }
}
