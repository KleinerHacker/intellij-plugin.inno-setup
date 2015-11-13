package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyStringValueElement extends IssPropertyValueElement {

    public IssPropertyStringValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    public String getString() {
        return getText().replace("\"", "");
    }
}
