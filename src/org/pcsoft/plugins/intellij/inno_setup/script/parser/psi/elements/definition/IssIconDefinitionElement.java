package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIconFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    public String getName() {
        return getIconName() == null ? null : getIconName().getPropertyValue() == null ? null : getIconName().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getIconName() {
        return IssFindUtils.findProperty(this, IssIconProperty.Name, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyIconFlagsElement getIconFlags() {
        return IssFindUtils.findProperty(this, IssIconProperty.Flags, IssPropertyIconFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getIconComponentReference() {
        return IssFindUtils.findProperty(this, IssIconProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getIconTaskReference() {
        return IssFindUtils.findProperty(this, IssIconProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
