package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public class IssEnvironmentConstantElement extends IssConstantElement {

    public IssEnvironmentConstantElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssConstantArgumentElement getDefaultValue() {
        return PsiTreeUtil.findChildOfType(this, IssConstantArgumentElement.class);
    }
}
