package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssPropertyTaskReferenceElement extends IssPropertyElement<IssPropertyTaskReferenceValueElement> {
    public IssPropertyTaskReferenceElement(ASTNode node, IssPropertyIdentifier propertyType) {
        super(node, IssPropertyTaskReferenceValueElement.class, propertyType);
    }
}
