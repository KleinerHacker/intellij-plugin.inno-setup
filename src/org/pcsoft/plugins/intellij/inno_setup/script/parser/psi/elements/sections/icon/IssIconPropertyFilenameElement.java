package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconPropertyFilenameElement extends IssDefinitionPropertyElement<IssIconDefinitionElement,IssIconPropertyFilenameValueElement,IssIconProperty> {
    public IssIconPropertyFilenameElement(ASTNode node) {
        super(node, IssIconDefinitionElement.class,IssIconPropertyFilenameValueElement.class);
    }

    @NotNull
    @Override
    public IssIconProperty getPropertyType() {
        return IssIconProperty.Filename;
    }
}
