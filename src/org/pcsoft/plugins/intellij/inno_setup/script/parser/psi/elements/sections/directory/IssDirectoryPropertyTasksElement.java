package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryProperty;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryPropertyTasksElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement, IssDirectoryPropertyTasksValueElement,IssDirectoryProperty> {
    public IssDirectoryPropertyTasksElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class, IssDirectoryPropertyTasksValueElement.class);
    }

    @NotNull
    @Override
    public IssDirectoryProperty getPropertyType() {
        return IssDirectoryProperty.Tasks;
    }
}
