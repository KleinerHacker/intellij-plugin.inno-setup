package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyTasksElement extends IssDefinitionPropertyElement<IssFileDefinitionElement, IssFilePropertyTasksValueElement> {
    public IssFilePropertyTasksElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class, IssFilePropertyTasksValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
