package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssValueElement extends IssAbstractElement {

    public IssValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText();
    }

    @NotNull
    public String[] getTextParts() {
        return getText().split(" ");
    }
}
