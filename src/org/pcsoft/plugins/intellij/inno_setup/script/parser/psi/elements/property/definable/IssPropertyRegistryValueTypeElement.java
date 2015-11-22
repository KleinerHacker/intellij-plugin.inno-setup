package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryValueTypeElement extends IssDefinablePropertyElement<IssPropertyRegistryValueTypeValueElement> {
    public IssPropertyRegistryValueTypeElement(ASTNode node) {
        super(node, IssPropertyRegistryValueTypeValueElement.class, IssRegistryProperty.ValueType);
    }
}
