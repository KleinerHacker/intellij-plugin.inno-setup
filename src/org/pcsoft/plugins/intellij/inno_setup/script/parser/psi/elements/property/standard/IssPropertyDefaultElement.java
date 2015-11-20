package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.standard;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssStandardPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyDefaultElement extends IssStandardPropertyElement<IssPropertyDefaultValueElement> {

    public IssPropertyDefaultElement(ASTNode node, @NotNull IssPropertyIdentifier propertyType) {
        super(node, IssPropertyDefaultValueElement.class, propertyType);
    }
}
