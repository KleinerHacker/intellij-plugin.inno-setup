package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssINIConstantElement extends IssConstantElement {

    public IssINIConstantElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssConstantNameElement getINIFilename() {
        return getConstantName();
    }

    @Nullable
    public IssConstantArgumentElement getINISection() {
        final ArrayList<IssConstantArgumentElement> elements = new ArrayList<>(PsiTreeUtil.findChildrenOfType(this, IssConstantArgumentElement.class));
        if (elements.size() <= 0)
            return null;

        return elements.get(0);
    }

    @Nullable
    public IssConstantArgumentElement getINIKey() {
        final ArrayList<IssConstantArgumentElement> elements = new ArrayList<>(PsiTreeUtil.findChildrenOfType(this, IssConstantArgumentElement.class));
        if (elements.size() <= 1)
            return null;

        return elements.get(1);
    }

    @Nullable
    public IssConstantArgumentElement getINIDefaultValue() {
        final ArrayList<IssConstantArgumentElement> elements = new ArrayList<>(PsiTreeUtil.findChildrenOfType(this, IssConstantArgumentElement.class));
        if (elements.size() <= 2)
            return null;

        return elements.get(2);
    }
}
