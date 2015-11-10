package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssFilePropertyComponentsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyComponentsValueElement, IssFileProperty> {
    public IssFilePropertyComponentsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyComponentsValueElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty getPropertyType() {
        return IssFileProperty.Components;
    }
}
