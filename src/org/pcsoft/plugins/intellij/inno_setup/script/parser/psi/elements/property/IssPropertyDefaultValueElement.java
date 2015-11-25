package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyDefaultValueElement extends IssPropertyValueElement {

    public IssPropertyDefaultValueElement(ASTNode node) {
        super(node);
    }
}
