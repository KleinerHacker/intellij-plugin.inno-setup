package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssDirectoryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyDirectoryFlagsElement extends IssPropertyElement<IssPropertyDirectoryFlagsValueElement> {

    public IssPropertyDirectoryFlagsElement(ASTNode node) {
        super(node, IssPropertyDirectoryFlagsValueElement.class, IssDirectoryProperty.Flags);
    }
}
