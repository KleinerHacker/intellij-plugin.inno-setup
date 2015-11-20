package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRunFlagsElement extends IssDefinablePropertyElement<IssPropertyRunFlagsValueElement> {
    public IssPropertyRunFlagsElement(ASTNode node) {
        super(node, IssPropertyRunFlagsValueElement.class, IssRunProperty.Flags);
    }
}
