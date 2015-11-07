package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyNameElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement> {

    public IssComponentPropertyNameElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class);
    }

    @Nullable
    public IssComponentPropertyNameValueElement getNameValue() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyNameValueElement.class);
    }
}
