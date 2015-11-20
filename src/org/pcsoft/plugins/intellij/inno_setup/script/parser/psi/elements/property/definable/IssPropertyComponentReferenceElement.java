package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssPropertyComponentReferenceElement extends IssDefinablePropertyElement<IssPropertyComponentReferenceValueElement> {
    public IssPropertyComponentReferenceElement(ASTNode node, IssDefinablePropertyIdentifier propertyType) {
        super(node, IssPropertyComponentReferenceValueElement.class, propertyType);
    }
}
