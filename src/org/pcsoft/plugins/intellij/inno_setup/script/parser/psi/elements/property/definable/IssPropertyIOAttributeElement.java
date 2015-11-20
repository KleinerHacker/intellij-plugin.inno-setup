package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssPropertyIOAttributeElement extends IssDefinablePropertyElement<IssPropertyIOAttributeValueElement> {
    public IssPropertyIOAttributeElement(ASTNode node, IssDefinablePropertyIdentifier propertyType) {
        super(node, IssPropertyIOAttributeValueElement.class, propertyType);
    }
}
