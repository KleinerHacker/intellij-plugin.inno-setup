package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssStringElement extends IssAbstractElement {

    public IssStringElement(ASTNode node) {
        super(node);
    }

    @NotNull
    public String getString() {
        return getText().replace("\"", "");
    }
}
