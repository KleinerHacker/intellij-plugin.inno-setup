package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryRootElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryValueTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssRegistrySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssRegistryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Registry;
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
