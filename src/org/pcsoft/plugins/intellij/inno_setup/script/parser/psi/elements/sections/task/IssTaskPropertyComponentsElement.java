package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

import java.util.Collection;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssTaskPropertyComponentsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement> {

    public IssTaskPropertyComponentsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class);
    }

    @NotNull
    public Collection<IssTaskPropertyComponentsValueElement> getComponentsValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssTaskPropertyComponentsValueElement.class);
    }
}
