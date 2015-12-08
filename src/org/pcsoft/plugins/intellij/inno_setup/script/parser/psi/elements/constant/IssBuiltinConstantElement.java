package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssBuiltinConstantElement extends IssConstantElement {

    public IssBuiltinConstantElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssBuiltinConstant getBuiltinConstant() {
        return IssBuiltinConstant.fromId(super.getName());
    }
}
