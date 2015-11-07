package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyNameElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement> {

    public IssTaskPropertyNameElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }

    @Nullable
    public IssTaskPropertyNameValueElement getNameValue() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyNameValueElement.class);
    }
}
