package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertySourceElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertySourceValueElement, IssFileProperty> {
    public IssFilePropertySourceElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertySourceValueElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty getPropertyType() {
        return IssFileProperty.Source;
    }
}
