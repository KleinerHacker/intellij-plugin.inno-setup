package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyWindowsVersionElement extends IssDefinablePropertyElement<IssPropertyWindowsVersionValueElement> {
    public IssPropertyWindowsVersionElement(ASTNode node, IssDefinablePropertyIdentifier propertyIdentifier) {
        super(node, IssPropertyWindowsVersionValueElement.class, propertyIdentifier);
    }
}
