package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;

import javax.swing.Icon;
import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public abstract class IssDefinitionElement<P extends IssDefinableSectionElement, T extends IssPropertyIdentifier> extends IssAbstractElement {

    private final Class<P> clazz;

    public IssDefinitionElement(ASTNode node, Class<P> clazz) {
        super(node);
        this.clazz = clazz;
    }

    @Nullable
    public Collection<IssPropertyElement> getDefinitionPropertyList() {
        return PsiTreeUtil.findChildrenOfType(this, IssPropertyElement.class);
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
                return getPresentableIcon();
            }
        };
    }

    @Nullable
    protected abstract String getDefinitionName();
    @NotNull
    protected abstract IssSectionType getSectionType();

    @Nullable
    protected Icon getPresentableIcon() {
        return getSectionType().getIcon();
    }
}
