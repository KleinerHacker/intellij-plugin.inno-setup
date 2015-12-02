package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssSectionHeaderElement extends IssAbstractElement {

    public IssSectionHeaderElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssSectionNameElement getSectionName() {
        return PsiTreeUtil.findChildOfType(this, IssSectionNameElement.class);
    }
}
