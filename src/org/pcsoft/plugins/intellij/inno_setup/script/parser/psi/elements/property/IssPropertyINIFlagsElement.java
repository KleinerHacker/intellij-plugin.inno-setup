package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssINIProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyINIFlagsElement extends IssPropertyElement<IssPropertyINIFlagsValueElement> {
    public IssPropertyINIFlagsElement(ASTNode node) {
        super(node, IssPropertyINIFlagsValueElement.class, IssINIProperty.Flags);
    }
}
