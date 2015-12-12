package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyAppIdElement extends IssPropertyElement<IssPropertyAppIdValueElement> {
    public IssPropertyAppIdElement(ASTNode node) {
        super(node, IssPropertyAppIdValueElement.class, IssSetupProperty.AppId);
    }
}
