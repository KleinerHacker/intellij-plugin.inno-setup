package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public abstract class IssConstantElement extends IssAbstractElement {

    public IssConstantElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText().replace("{", "").replace("}", "");
    }
}
