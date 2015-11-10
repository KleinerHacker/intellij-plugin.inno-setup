package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssFilePropertyFlagsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement, IssFilePropertyFlagsValueElement, IssFileProperty> {

    public IssFilePropertyFlagsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class, IssFilePropertyFlagsValueElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty getPropertyType() {
        return IssFileProperty.Flags;
    }
}
