package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

import java.util.Collection;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public abstract class IssConstantElement extends IssAbstractElement {

    public IssConstantElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText().replace("{", "").replace("}", "");
    }

    @Nullable
    public IssConstantNameElement getConstantName() {
        return PsiTreeUtil.findChildOfType(this, IssConstantNameElement.class);
    }

    @Nullable
    public IssConstantTypeElement getConstantType() {
        return PsiTreeUtil.findChildOfType(this, IssConstantTypeElement.class);
    }

    @Nullable
    public IssConstantDefaultValueElement getConstantDefaultValue() {
        return PsiTreeUtil.findChildOfType(this, IssConstantDefaultValueElement.class);
    }

    @NotNull
    public Collection<IssConstantArgumentElement> getConstantArgumentList() {
        return PsiTreeUtil.findChildrenOfType(this, IssConstantArgumentElement.class);
    }
}
