package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyTasksElement extends IssDefinitionPropertyElement<IssFileDefinitionElement, IssFilePropertyTasksValueElement, IssFileProperty> {
    public IssFilePropertyTasksElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class, IssFilePropertyTasksValueElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty getPropertyType() {
        return IssFileProperty.Tasks;
    }
}
