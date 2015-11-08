package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssDirectoryPropertyComponentsElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement,IssDirectoryPropertyComponentsValueElement> {
    public IssDirectoryPropertyComponentsElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class,IssDirectoryPropertyComponentsValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
