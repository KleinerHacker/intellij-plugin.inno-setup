package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssRegistrySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssRegistryDefinitionElement extends IssDefinitionElement<IssRegistrySectionElement, IssRegistryProperty> {

    public IssRegistryDefinitionElement(ASTNode node) {
        super(node, IssRegistrySectionElement.class);
    }

    @NotNull
    @Override
    public IssRegistryProperty[] getPropertyTypeList() {
        return IssRegistryProperty.values();
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
                return "Regsitry";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_REGISTRY;
            }
        };
    }

    @Nullable
    @Override
    public String getName() {
        final StringBuilder sb = new StringBuilder();
        if (getRegistryRoot() != null && getRegistryRoot().getPropertyValue() != null && getRegistryRoot().getPropertyValue().getRootType() != null) {
            sb.append(getRegistryRoot().getPropertyValue().getRootType().getId()).append("\\");
        }
        if (getRegistrySubkey() != null && getRegistrySubkey().getPropertyValue() != null) {
            sb.append(getRegistrySubkey().getPropertyValue().getString()).append("\\");
        }
        if (getRegistryValueName() != null && getRegistryValueName().getPropertyValue() != null) {
            sb.append(getRegistryValueName().getPropertyValue().getString());
        }

        return sb.toString();
    }

    @Nullable
    public IssPropertyRegistryRootElement getRegistryRoot() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.Root, IssPropertyRegistryRootElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRegistrySubkey() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.Subkey, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyRegistryValueTypeElement getRegistryValueType() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.ValueType, IssPropertyRegistryValueTypeElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRegistryValueName() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.ValueName, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRegistryValueData() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.ValueData, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyIOPermissionsElement getRegistryPermissions() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.Permissions, IssPropertyIOPermissionsElement.class);
    }

    @Nullable
    public IssPropertyRegistryFlagsElement getRegistryFlags() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.Flags, IssPropertyRegistryFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getRegistryComponentReference() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getRegistryTaskReference() {
        return IssFindUtils.findProperty(this, IssRegistryProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
