package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssPropertyIOCopyModeElement extends IssPropertyElement<IssPropertyIOCopyModeValueElement> {
    public IssPropertyIOCopyModeElement(ASTNode node, IssPropertyIdentifier propertyType) {
        super(node, IssPropertyIOCopyModeValueElement.class, propertyType);
    }
}
