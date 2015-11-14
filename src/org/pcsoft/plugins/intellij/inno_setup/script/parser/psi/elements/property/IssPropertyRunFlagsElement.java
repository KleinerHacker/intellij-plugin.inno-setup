package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRunFlagsElement extends IssPropertyElement<IssPropertyRunFlagsValueElement> {
    public IssPropertyRunFlagsElement(ASTNode node) {
        super(node, IssPropertyRunFlagsValueElement.class, IssRunProperty.Flags);
    }
}
