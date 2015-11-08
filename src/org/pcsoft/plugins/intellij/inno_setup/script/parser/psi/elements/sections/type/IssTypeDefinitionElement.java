package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;

import javax.swing.Icon;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypeDefinitionElement extends IssDefinitionElement<IssTypeSectionElement> {

    public IssTypeDefinitionElement(ASTNode node) {
        super(node, IssTypeSectionElement.class);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "Types";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return null;
            }
        };
    }

    @Override
    public String getName() {
        return getTypeName() == null ? null : getTypeName().getPropertyValue() == null ? null : getTypeName().getPropertyValue().getName();
    }

    @Nullable
    public IssTypePropertyNameElement getTypeName() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyNameElement.class);
    }

    @Nullable
    public IssTypePropertyDescriptionElement getTypeDescription() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyDescriptionElement.class);
    }

    @Nullable
    public IssTypePropertyFlagsElement getTypeFlags() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyFlagsElement.class);
    }
}
