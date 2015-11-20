package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyIntegerValueElement extends IssPropertyValueElement {

    public IssPropertyIntegerValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Integer getNumber() {
        try {
            return Integer.valueOf(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
