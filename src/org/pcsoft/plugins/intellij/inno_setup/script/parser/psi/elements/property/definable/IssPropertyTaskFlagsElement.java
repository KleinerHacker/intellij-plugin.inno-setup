package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyTaskFlagsElement extends IssDefinablePropertyElement<IssPropertyTaskFlagsValueElement> {
    public IssPropertyTaskFlagsElement(ASTNode node) {
        super(node, IssPropertyTaskFlagsValueElement.class, IssTaskProperty.Flags);
    }
}
