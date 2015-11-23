package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;

import javax.swing.Icon;

/**
 * Created by Christoph on 15.12.2014.
 */
public abstract class IssSectionElement extends IssAbstractElement {

    public IssSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssSectionType getSectionType() {
        return getSectionNameElement() == null ? null : IssSectionType.fromId(getSectionNameElement().getName());
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getSectionType() == null ? "<UNKNOWN>" : getSectionType().getId();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getContainingFile() == null ? "<UNKNOWN>" : getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return getSectionType() == null ? null : getSectionType().getIcon();
            }
        };
    }

    @Nullable
    public IssSectionNameElement getSectionNameElement() {
        return PsiTreeUtil.findChildOfType(this, IssSectionNameElement.class);
    }
}
