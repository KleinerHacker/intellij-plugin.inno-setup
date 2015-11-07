package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypePropertyNameElement extends IssDefinitionPropertyElement<IssTypeDefinitionElement> {

    public IssTypePropertyNameElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class);
    }

    @Nullable
    public IssTypePropertyNameValueElement getNameValue() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyNameValueElement.class);
    }
}
