package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyWindowsVersionValueElement extends IssPropertyValueElement {

    public IssPropertyWindowsVersionValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    public String getVersion() {
        return getText();
    }
}
