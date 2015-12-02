package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssTypeProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyTypeFlagsElement extends IssPropertyElement<IssPropertyTypeFlagsValueElement> {
    public IssPropertyTypeFlagsElement(ASTNode node) {
        super(node, IssPropertyTypeFlagsValueElement.class, IssTypeProperty.Flags);
    }
}
