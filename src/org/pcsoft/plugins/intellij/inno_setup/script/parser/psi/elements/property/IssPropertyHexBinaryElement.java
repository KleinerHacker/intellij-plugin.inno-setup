package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyHexBinaryElement extends IssPropertyElement<IssPropertyHexBinaryValueElement> {
    public IssPropertyHexBinaryElement(ASTNode node, IssPropertyIdentifier propertyType) {
        super(node, IssPropertyHexBinaryValueElement.class, propertyType);
    }
}
