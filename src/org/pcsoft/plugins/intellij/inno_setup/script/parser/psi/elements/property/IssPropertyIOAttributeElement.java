package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssPropertyIOAttributeElement extends IssPropertyElement<IssPropertyIOAttributeValueElement> {
    public IssPropertyIOAttributeElement(ASTNode node, IssDefinableSectionIdentifier propertyType) {
        super(node, IssPropertyIOAttributeValueElement.class, propertyType);
    }
}
