package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;

import javax.swing.Icon;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentDefinitionElement extends IssDefinitionElement<IssComponentSectionElement> {

    public IssComponentDefinitionElement(ASTNode node) {
        super(node, IssComponentSectionElement.class);
    }

    @Nullable
    public IssComponentPropertyNameElement getComponentName() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyNameElement.class);
    }

    @Nullable
    public IssComponentPropertyDescriptionElement getComponentDescription() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyDescriptionElement.class);
    }

    @Nullable
    public IssComponentPropertyExtraDiskSpaceRequiredElement getComponentExtraDiskSpaceRequired() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyExtraDiskSpaceRequiredElement.class);
    }

    @Nullable
    public IssComponentPropertyTypesElement getComponentTypes() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyTypesElement.class);
    }

    @Nullable
    public IssComponentPropertyFlagsElement getComponentFlags() {
        return PsiTreeUtil.findChildOfType(this, IssComponentPropertyFlagsElement.class);
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
                return "Components";
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
        return getComponentName() == null ? null : getComponentName().getPropertyValue() == null ? null : getComponentName().getPropertyValue().getName();
    }
}
