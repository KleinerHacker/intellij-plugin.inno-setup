package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssINIProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyINIFlagsElement extends IssDefinablePropertyElement<IssPropertyINIFlagsValueElement> {
    public IssPropertyINIFlagsElement(ASTNode node) {
        super(node, IssPropertyINIFlagsValueElement.class, IssINIProperty.Flags);
    }
}
