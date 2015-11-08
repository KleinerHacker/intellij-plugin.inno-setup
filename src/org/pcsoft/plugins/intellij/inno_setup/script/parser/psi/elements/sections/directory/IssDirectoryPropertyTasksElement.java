package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryPropertyTasksElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement, IssDirectoryPropertyTasksValueElement> {
    public IssDirectoryPropertyTasksElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class, IssDirectoryPropertyTasksValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
