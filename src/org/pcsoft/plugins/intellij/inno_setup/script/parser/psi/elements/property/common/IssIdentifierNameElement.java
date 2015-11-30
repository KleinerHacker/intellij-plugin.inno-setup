package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 30.11.2015.
 */
public class IssIdentifierNameElement extends IssAbstractElement {

    public IssIdentifierNameElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}
