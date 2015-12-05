package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssConstantType;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public class IssConstantTypeElement extends IssAbstractElement {
    public IssConstantTypeElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    public IssConstantType getType() {
        return IssConstantType.fromId(getName());
    }
}
