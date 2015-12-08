package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssConstantDefaultValueElement extends IssAbstractElement {

    public IssConstantDefaultValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssConstantElement getConstant() {
        return PsiTreeUtil.getParentOfType(this, IssConstantElement.class);
    }
}
