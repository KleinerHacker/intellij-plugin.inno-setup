package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.apache.commons.lang.BooleanUtils;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyBooleanValueElement extends IssPropertyValueElement {

    public IssPropertyBooleanValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Boolean getState() {
        try {
            return BooleanUtils.toBoolean(getText().toLowerCase(), "yes", "no");
        } catch (Exception e) {
            return null;
        }
    }
}
