package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssIconProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyIconFlagsElement extends IssPropertyElement<IssPropertyIconFlagsValueElement> {
    public IssPropertyIconFlagsElement(ASTNode node) {
        super(node, IssPropertyIconFlagsValueElement.class, IssIconProperty.Flags);
    }
}
