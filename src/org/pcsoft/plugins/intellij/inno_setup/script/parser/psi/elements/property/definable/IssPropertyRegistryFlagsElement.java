package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryFlagsElement extends IssDefinablePropertyElement<IssPropertyRegistryFlagsValueElement> {
    public IssPropertyRegistryFlagsElement(ASTNode node) {
        super(node, IssPropertyRegistryFlagsValueElement.class, IssRegistryProperty.Flags);
    }
}
