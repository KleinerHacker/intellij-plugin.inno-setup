package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyIconFlagsElement extends IssDefinablePropertyElement<IssPropertyIconFlagsValueElement> {
    public IssPropertyIconFlagsElement(ASTNode node) {
        super(node, IssPropertyIconFlagsValueElement.class, IssIconProperty.Flags);
    }
}
