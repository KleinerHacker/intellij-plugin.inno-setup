package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssValueElement;

import java.util.Collection;

/**
 * Created by Christoph on 22.12.2014.
 */
public abstract class IssDefinitionPropertyElement<D extends IssDefinitionElement, V extends IssDefinitionPropertyValueElement> extends IssAbstractElement {

    private final Class<D> definitionClass;
    private final Class<V> valueClass;

    public IssDefinitionPropertyElement(ASTNode node, Class<D> definitionClass, Class<V> valueClass) {
        super(node);
        this.definitionClass = definitionClass;
        this.valueClass = valueClass;
    }

    @Nullable
    public final IssIdentifierElement getIdentifier() {
        return PsiTreeUtil.findChildOfType(this, IssIdentifierElement.class);
    }

    @Nullable
    public final IssValueElement getValue() {
        return PsiTreeUtil.findChildOfType(this, IssValueElement.class);
    }

    @Nullable
    public final D getDefinition() {
        if (definitionClass == null)
            throw new IllegalStateException("Method call not supported");

        return PsiTreeUtil.getParentOfType(this, definitionClass);
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
    public abstract IssItemValueType getItemValueType();
}
