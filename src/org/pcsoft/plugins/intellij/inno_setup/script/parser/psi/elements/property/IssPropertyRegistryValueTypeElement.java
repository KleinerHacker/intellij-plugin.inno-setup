package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssRegistryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryValueTypeElement extends IssPropertyElement<IssPropertyRegistryValueTypeValueElement> {
    public IssPropertyRegistryValueTypeElement(ASTNode node) {
        super(node, IssPropertyRegistryValueTypeValueElement.class, IssRegistryProperty.ValueType);
    }
}
