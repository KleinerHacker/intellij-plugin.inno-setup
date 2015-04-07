package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileDefinitionDestDirElement extends IssDefinitionItemElement<IssFileDefinitionElement> {

    public IssFileDefinitionDestDirElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class);
    }

    @Nullable
    public IssFileDefinitionDestDirValueElement getDestDirValue() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionDestDirValueElement.class);
    }
}
