package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyDoubleValueElement extends IssPropertyValueElement {

    public IssPropertyDoubleValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Double getNumber() {
        try {
            return Double.valueOf(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
