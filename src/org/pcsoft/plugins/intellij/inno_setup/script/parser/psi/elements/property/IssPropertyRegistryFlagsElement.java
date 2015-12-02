package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssRegistryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryFlagsElement extends IssPropertyElement<IssPropertyRegistryFlagsValueElement> {
    public IssPropertyRegistryFlagsElement(ASTNode node) {
        super(node, IssPropertyRegistryFlagsValueElement.class, IssRegistryProperty.Flags);
    }
}
