package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyComponentFlagsElement extends IssDefinablePropertyElement<IssPropertyComponentFlagsValueElement> {
    public IssPropertyComponentFlagsElement(ASTNode node) {
        super(node, IssPropertyComponentFlagsValueElement.class, IssComponentProperty.Flags);
    }
}