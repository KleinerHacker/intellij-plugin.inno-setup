package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssDirectoryPropertyFlagsElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement, IssDirectoryPropertyFlagsValueElement,IssDirectoryProperty> {

    public IssDirectoryPropertyFlagsElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class, IssDirectoryPropertyFlagsValueElement.class);
    }

    @NotNull
    @Override
    public IssDirectoryProperty getPropertyType() {
        return IssDirectoryProperty.Flags;
    }
}
