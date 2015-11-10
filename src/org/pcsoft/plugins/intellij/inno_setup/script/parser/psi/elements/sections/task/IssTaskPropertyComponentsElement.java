package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssTaskPropertyComponentsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssTaskPropertyComponentsValueElement,IssTaskProperty> {
    public IssTaskPropertyComponentsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssTaskPropertyComponentsValueElement.class);
    }

    @NotNull
    @Override
    public IssTaskProperty getPropertyType() {
        return IssTaskProperty.Components;
    }
}
