package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public abstract class IssDefinitionElement<P extends IssDefinableSectionElement, T extends IssDefinablePropertyIdentifier> extends IssAbstractElement {

    private final Class<P> clazz;

    public IssDefinitionElement(ASTNode node, Class<P> clazz) {
        super(node);
        this.clazz = clazz;
    }

    @Nullable
    public Collection<IssDefinablePropertyElement> getDefinitionPropertyList() {
        return PsiTreeUtil.findChildrenOfType(this, IssDefinablePropertyElement.class);
    }

    @Nullable
    public P getParentSection() {
        return PsiTreeUtil.getParentOfType(this, clazz);
    }

    @NotNull
    public abstract T[] getPropertyTypeList();

    @Override
    public final ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getDefinitionName() == null ? "<UNKNOWN>" : getDefinitionName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getSectionType().getId();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return getSectionType().getIcon();
            }
        };
    }

    @Nullable
    protected abstract String getDefinitionName();
    @NotNull
    protected abstract IssSectionType getSectionType();
}
