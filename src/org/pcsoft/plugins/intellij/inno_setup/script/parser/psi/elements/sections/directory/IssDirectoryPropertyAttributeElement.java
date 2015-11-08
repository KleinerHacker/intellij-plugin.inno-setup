package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryPropertyAttributeElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement,IssDirectoryPropertyAttributeValueElement> {
    public IssDirectoryPropertyAttributeElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class,IssDirectoryPropertyAttributeValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}
