package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
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
                return getFirstChild().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssSectionElement.this.getIcon(b);
            }
        };
    }

    @Nullable
    public IssSectionNameElement getSectionNameElement() {
        return PsiTreeUtil.findChildOfType(this, IssSectionNameElement.class);
    }

    protected abstract Icon getIcon(boolean b);
}
