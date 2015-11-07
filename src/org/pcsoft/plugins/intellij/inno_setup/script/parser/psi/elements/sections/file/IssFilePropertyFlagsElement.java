package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

import java.util.Collection;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssFilePropertyFlagsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement> {

    public IssFilePropertyFlagsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class);
    }

    @NotNull
    public Collection<IssFilePropertyFlagsValueElement> getFlagsValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssFilePropertyFlagsValueElement.class);
    }
}
