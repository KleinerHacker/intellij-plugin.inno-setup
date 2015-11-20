package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public abstract class IssStandardPropertyElement<V extends IssPropertyValueElement> extends IssPropertyElement<V, IssPropertyIdentifier> {

    public IssStandardPropertyElement(ASTNode node, @NotNull Class<V> valueClass, @NotNull IssPropertyIdentifier propertyType) {
        super(node, valueClass, propertyType);
    }
}
