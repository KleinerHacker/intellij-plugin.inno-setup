package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Created by Christoph on 20.11.2015.
 */
public abstract class IssStandardSectionElement extends IssSectionElement {

    public IssStandardSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Collection<IssStandardPropertyElement> getSectionPropertyList() {
        return PsiTreeUtil.findChildrenOfType(this, IssStandardPropertyElement.class);
    }

}
