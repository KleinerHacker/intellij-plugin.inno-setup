package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyCompressionElement extends IssPropertyElement<IssPropertyCompressionValueElement> {

    public IssPropertyCompressionElement(ASTNode node, @NotNull IssPropertyIdentifier propertyType) {
        super(node, IssPropertyCompressionValueElement.class, propertyType);
    }
}
