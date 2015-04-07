package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileDefinitionSourceElement extends IssDefinitionItemElement<IssFileDefinitionElement> {

    public IssFileDefinitionSourceElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class);
    }

    @Nullable
    public IssFileDefinitionSourceValueElement getSourceValue() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionSourceValueElement.class);
    }
}
