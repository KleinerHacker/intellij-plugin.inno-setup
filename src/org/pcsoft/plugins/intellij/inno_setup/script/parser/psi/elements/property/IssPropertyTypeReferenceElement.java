package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyTypeReferenceElement extends IssPropertyElement<IssPropertyTypeReferenceValueElement> {
    public IssPropertyTypeReferenceElement(ASTNode node, IssPropertyIdentifier propertyType) {
        super(node, IssPropertyTypeReferenceValueElement.class, propertyType);
    }
}
