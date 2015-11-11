package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssIconDefinitionElement extends IssDefinitionElement<IssIconSectionElement, IssIconProperty> {

    public IssIconDefinitionElement(ASTNode node) {
        super(node, IssIconSectionElement.class);
    }

    @NotNull
    @Override
    public IssIconProperty[] getPropertyTypeList() {
        return IssIconProperty.values();
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
                return "Icons";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_ICON;
            }
        };
    }

    @Override
    public String getName() {
        return getIconName() == null ? null : getIconName().getPropertyValue() == null ? null : getIconName().getPropertyValue().getName();
    }

    @Nullable
    public IssIconPropertyNameElement getIconName() {
        return PsiTreeUtil.findChildOfType(this, IssIconPropertyNameElement.class);
    }

    @Nullable
    public IssIconPropertyFlagsElement getIconFlags() {
        return PsiTreeUtil.findChildOfType(this, IssIconPropertyFlagsElement.class);
    }

    @Nullable
    public IssIconPropertyComponentsElement getIconComponents() {
        return PsiTreeUtil.findChildOfType(this, IssIconPropertyComponentsElement.class);
    }

    @Nullable
    public IssIconPropertyTasksElement getIconTasks() {
        return PsiTreeUtil.findChildOfType(this, IssIconPropertyTasksElement.class);
    }
}
