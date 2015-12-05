package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public class IssConstantArgumentElement extends IssAbstractElement {

    public IssConstantArgumentElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    public IssConstantElement getConstant() {
        return PsiTreeUtil.getParentOfType(this, IssConstantElement.class);
    }
}
