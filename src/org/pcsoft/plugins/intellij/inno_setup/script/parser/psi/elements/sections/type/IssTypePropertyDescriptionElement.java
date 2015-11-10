package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypePropertyDescriptionElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement,IssTypePropertyDescriptionValueElement,IssTypeProperty> {
    public IssTypePropertyDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class,IssTypePropertyDescriptionValueElement.class);
    }

    @NotNull
    @Override
    public IssTypeProperty getPropertyType() {
        return IssTypeProperty.Description;
    }
}
