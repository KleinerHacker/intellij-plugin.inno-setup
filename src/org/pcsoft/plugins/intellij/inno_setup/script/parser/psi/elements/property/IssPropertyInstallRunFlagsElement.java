package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyInstallRunFlagsElement extends IssPropertyElement<IssPropertyInstallRunFlagsValueElement> {
    public IssPropertyInstallRunFlagsElement(ASTNode node) {
        super(node, IssPropertyInstallRunFlagsValueElement.class, IssInstallRunProperty.Flags);
    }
}
