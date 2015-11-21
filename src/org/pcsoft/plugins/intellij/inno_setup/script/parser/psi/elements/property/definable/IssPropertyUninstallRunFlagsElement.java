package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssUninstallRunProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyUninstallRunFlagsElement extends IssDefinablePropertyElement<IssPropertyUninstallRunFlagsValueElement> {
    public IssPropertyUninstallRunFlagsElement(ASTNode node) {
        super(node, IssPropertyUninstallRunFlagsValueElement.class, IssUninstallRunProperty.Flags);
    }
}
