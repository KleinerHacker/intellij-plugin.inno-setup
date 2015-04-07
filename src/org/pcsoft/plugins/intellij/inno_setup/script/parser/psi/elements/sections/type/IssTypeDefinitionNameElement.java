package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeDefinitionNameElement extends IssDefinitionItemElement<IssTypeDefinitionElement> {

    public IssTypeDefinitionNameElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class);
    }

    @Nullable
    public IssTypeDefinitionNameValueElement getNameValue() {
        return PsiTreeUtil.findChildOfType(this, IssTypeDefinitionNameValueElement.class);
    }
}
