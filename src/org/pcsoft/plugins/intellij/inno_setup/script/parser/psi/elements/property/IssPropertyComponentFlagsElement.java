package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyComponentFlagsElement extends IssPropertyElement<IssPropertyComponentFlagsValueElement> {
    public IssPropertyComponentFlagsElement(ASTNode node) {
        super(node, IssPropertyComponentFlagsValueElement.class, IssComponentProperty.Flags);
    }
}
