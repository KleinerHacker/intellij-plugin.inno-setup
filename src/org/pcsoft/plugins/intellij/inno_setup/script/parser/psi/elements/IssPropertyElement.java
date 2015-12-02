package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

import java.util.Collection;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssPropertyElement<V extends IssPropertyValueElement> extends IssAbstractElement {
    private final Class<V> valueClass;
    private final IssPropertyIdentifier propertyType;

    public IssPropertyElement(ASTNode node, @NotNull Class<V> valueClass, @NotNull IssPropertyIdentifier propertyType) {
        super(node);
        this.valueClass = valueClass;
        this.propertyType = propertyType;
    }

    @Nullable
    public final IssIdentifierElement getIdentifier() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierElement.class);
    }

    @Nullable
    public final IssValueElement getValue() {
        return PsiTreeUtil.findChildOfType(this, IssValueElement.class);
    }

    @NotNull
    public final Collection<V> getPropertyValueList() {
        return PsiTreeUtil.findChildrenOfType(this, valueClass);
    }

    @Nullable
    public final V getPropertyValue() {
        return getPropertyValueList().stream().findFirst().orElse(null);
    }

    @NotNull
    public final IssPropertyIdentifier getPropertyType() {
        return propertyType;
    }

    @Nullable
    public final IssDefinitionElement getDefinition() {
        return PsiTreeUtil.getParentOfType(this, IssDefinitionElement.class);
    }

    public final boolean isUnderDefinition() {
        if (getSection() == null) {
            return getDefinition() != null;
        } else {
            return getSection() instanceof IssDefinableSectionElement;
        }
    }

    @Nullable
    public final IssSectionElement getSection() {
        return PsiTreeUtil.getParentOfType(this, IssSectionElement.class);
    }


}
