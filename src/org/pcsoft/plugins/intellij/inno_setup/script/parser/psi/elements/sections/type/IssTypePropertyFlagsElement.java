package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypePropertyFlagsElement extends IssDefinitionPropertyElement<IssTypeDefinitionElement,IssTypePropertyFlagsValueElement,IssTypeProperty> {
    public IssTypePropertyFlagsElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class,IssTypePropertyFlagsValueElement.class);
    }

    @NotNull
    @Override
    public IssTypeProperty getPropertyType() {
        return IssTypeProperty.Flags;
    }
}
