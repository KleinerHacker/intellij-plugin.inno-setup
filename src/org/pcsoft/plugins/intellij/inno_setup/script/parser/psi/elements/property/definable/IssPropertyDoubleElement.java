package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyDoubleElement extends IssDefinablePropertyElement<IssPropertyDoubleValueElement> {
    public IssPropertyDoubleElement(ASTNode node, IssDefinablePropertyIdentifier propertyType) {
        super(node, IssPropertyDoubleValueElement.class, propertyType);
    }
}
