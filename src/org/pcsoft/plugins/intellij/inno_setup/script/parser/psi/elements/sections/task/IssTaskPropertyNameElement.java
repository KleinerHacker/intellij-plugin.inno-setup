package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyNameElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement,IssTaskPropertyNameValueElement,IssTaskProperty> {
    public IssTaskPropertyNameElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class,IssTaskPropertyNameValueElement.class);
    }

    @NotNull
    @Override
    public IssTaskProperty getPropertyType() {
        return IssTaskProperty.Name;
    }
}
