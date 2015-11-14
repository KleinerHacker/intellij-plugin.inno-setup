package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyFileFlagsElement extends IssPropertyElement<IssPropertyFileFlagsValueElement> {
    public IssPropertyFileFlagsElement(ASTNode node) {
        super(node, IssPropertyFileFlagsValueElement.class, IssFileProperty.Flags);
    }
}