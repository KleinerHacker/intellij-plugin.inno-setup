package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssIconPropertyTasksElement extends IssDefinitionPropertyElement<IssIconDefinitionElement, IssIconPropertyTasksValueElement, IssIconProperty> {
    public IssIconPropertyTasksElement(ASTNode node) {
        super(node, IssIconDefinitionElement.class, IssIconPropertyTasksValueElement.class);
    }

    @NotNull
    @Override
    public IssIconProperty getPropertyType() {
        return IssIconProperty.Tasks;
    }
}
