package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyDirectoryFlagsElement extends IssDefinablePropertyElement<IssPropertyDirectoryFlagsValueElement> {

    public IssPropertyDirectoryFlagsElement(ASTNode node) {
        super(node, IssPropertyDirectoryFlagsValueElement.class, IssDirectoryProperty.Flags);
    }
}
