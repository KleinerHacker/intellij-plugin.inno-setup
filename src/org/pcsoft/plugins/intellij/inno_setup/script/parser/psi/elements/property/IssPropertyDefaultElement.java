package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyDefaultElement extends IssPropertyElement<IssPropertyDefaultValueElement> {

    public IssPropertyDefaultElement(ASTNode node, @NotNull IssPropertyIdentifier propertyType) {
        super(node, IssPropertyDefaultValueElement.class, propertyType);
    }
}
