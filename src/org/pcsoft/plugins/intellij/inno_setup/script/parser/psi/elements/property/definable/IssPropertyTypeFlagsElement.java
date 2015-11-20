package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyTypeFlagsElement extends IssDefinablePropertyElement<IssPropertyTypeFlagsValueElement> {
    public IssPropertyTypeFlagsElement(ASTNode node) {
        super(node, IssPropertyTypeFlagsValueElement.class, IssTypeProperty.Flags);
    }
}
