package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssFileLinkElement extends IssAbstractElement {

    public IssFileLinkElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText().replace("<", "").replace(">", "");
    }
}
