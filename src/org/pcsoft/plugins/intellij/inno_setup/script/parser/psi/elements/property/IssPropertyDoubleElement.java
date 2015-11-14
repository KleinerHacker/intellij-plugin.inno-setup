package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyDoubleElement extends IssPropertyElement<IssPropertyDoubleValueElement> {
    public IssPropertyDoubleElement(ASTNode node, IssDefinableSectionIdentifier propertyType) {
        super(node, IssPropertyDoubleValueElement.class, propertyType);
    }
}