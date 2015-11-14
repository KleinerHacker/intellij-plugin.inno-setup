package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssPropertyComponentReferenceElement extends IssPropertyElement<IssPropertyComponentReferenceValueElement> {
    public IssPropertyComponentReferenceElement(ASTNode node, IssDefinableSectionIdentifier propertyType) {
        super(node, IssPropertyComponentReferenceValueElement.class, propertyType);
    }
}
