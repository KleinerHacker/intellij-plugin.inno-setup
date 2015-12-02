package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDirectoryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssDirectorySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssDirectoryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryDefinitionElement extends IssDefinitionElement<IssDirectorySectionElement, IssDirectoryProperty> {

    public IssDirectoryDefinitionElement(ASTNode node) {
        super(node, IssDirectorySectionElement.class);
    }

    @NotNull
    @Override
    public IssDirectoryProperty[] getPropertyTypeList() {
        return IssDirectoryProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Directory;
    }

    @Nullable
    @Override
    public String getName() {
        return getDirectoryName() == null ? null : getDirectoryName().getPropertyValue() == null ? null : getDirectoryName().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getDirectoryName() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.Name, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyDirectoryFlagsElement getDirectoryFlags() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.Flags, IssPropertyDirectoryFlagsElement.class);
    }

    @Nullable
    public IssPropertyIOAttributeElement getDirectoryAttribute() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.Attributes, IssPropertyIOAttributeElement.class);
    }

    @Nullable
    public IssPropertyIOPermissionsElement getDirectoryPermissions() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.Permissions, IssPropertyIOPermissionsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getDirectoryComponentReference() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getDirectoryTaskReference() {
        return IssFindUtils.findProperty(this, IssDirectoryProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
