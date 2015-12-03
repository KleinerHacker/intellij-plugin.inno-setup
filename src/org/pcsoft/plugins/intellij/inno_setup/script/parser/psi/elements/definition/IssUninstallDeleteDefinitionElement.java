package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDeleteTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssUninstallDeleteSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssUninstallDeleteProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssUninstallDeleteDefinitionElement extends IssDefinitionElement<IssUninstallDeleteSectionElement, IssUninstallDeleteProperty> {

    public IssUninstallDeleteDefinitionElement(ASTNode node) {
        super(node, IssUninstallDeleteSectionElement.class);
    }

    @NotNull
    @Override
    public IssUninstallDeleteProperty[] getPropertyTypeList() {
        return IssUninstallDeleteProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.UninstallDelete;
    }

    @Nullable
    @Override
    public String getName() {
        return getUninstallDeleteName() == null ? null : getUninstallDeleteName().getPropertyValue() == null ? null : getUninstallDeleteName().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyDeleteTypeElement getUninstallDeleteType() {
        return IssFindUtils.findProperty(this, IssUninstallDeleteProperty.Type, IssPropertyDeleteTypeElement.class);
    }

    @Nullable
    public IssPropertyStringElement getUninstallDeleteName() {
        return IssFindUtils.findProperty(this, IssUninstallDeleteProperty.Name, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getUninstallDeleteComponentReference() {
        return IssFindUtils.findProperty(this, IssUninstallDeleteProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getUninstallDeleteTaskReference() {
        return IssFindUtils.findProperty(this, IssUninstallDeleteProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
