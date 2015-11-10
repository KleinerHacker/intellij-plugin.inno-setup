package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

import javax.swing.Icon;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssTaskDefinitionElement extends IssDefinitionElement<IssTaskSectionElement, IssTaskProperty> {

    public IssTaskDefinitionElement(ASTNode node) {
        super(node, IssTaskSectionElement.class);
    }

    @NotNull
    @Override
    public IssTaskProperty[] getPropertyTypeList() {
        return IssTaskProperty.values();
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
                return "Tasks";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_TASK;
            }
        };
    }

    @Override
    public String getName() {
        return getTaskName() == null ? null : getTaskName().getPropertyValue() == null ? null : getTaskName().getPropertyValue().getName();
    }

    @Nullable
    public IssTaskPropertyNameElement getTaskName() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyNameElement.class);
    }

    @Nullable
    public IssTaskPropertyDescriptionElement getTaskDescription() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyDescriptionElement.class);
    }

    @Nullable
    public IssTaskPropertyComponentsElement getTaskComponents() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyComponentsElement.class);
    }

    @Nullable
    public IssTaskPropertyFlagsElement getTaskFlags() {
        return PsiTreeUtil.findChildOfType(this, IssTaskPropertyFlagsElement.class);
    }
}
