package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;

/**
 * Created by Christoph on 20.11.2015.
 */
public abstract class IssDefinablePropertyElement<V extends IssPropertyValueElement> extends IssPropertyElement<V, IssDefinablePropertyIdentifier> {

    public IssDefinablePropertyElement(ASTNode node, @NotNull Class<V> valueClass, @NotNull IssDefinablePropertyIdentifier propertyType) {
        super(node, valueClass, propertyType);
    }

    @Nullable
    public final IssDefinitionElement getDefinition() {
        return PsiTreeUtil.getParentOfType(this, IssDefinitionElement.class);
    }
}
