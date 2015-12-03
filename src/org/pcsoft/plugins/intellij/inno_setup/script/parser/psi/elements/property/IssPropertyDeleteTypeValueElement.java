package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssDeleteType;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyDeleteTypeValueElement extends IssPropertyValueElement {

    public IssPropertyDeleteTypeValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    public IssDeleteType getDeleteType() {
        return IssDeleteType.fromId(getText());
    }
}
