package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypePropertyNameElement extends IssDefinitionPropertyElement<IssTypeDefinitionElement,IssTypePropertyNameValueElement,IssTypeProperty> {
    public IssTypePropertyNameElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class,IssTypePropertyNameValueElement.class);
    }

    @NotNull
    @Override
    public IssTypeProperty getPropertyType() {
        return IssTypeProperty.Name;
    }
}
