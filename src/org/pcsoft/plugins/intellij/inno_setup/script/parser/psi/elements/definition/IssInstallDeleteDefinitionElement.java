package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDeleteTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssInstallDeleteSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssInstallDeleteProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssInstallDeleteDefinitionElement extends IssDefinitionElement<IssInstallDeleteSectionElement, IssInstallDeleteProperty> {

    public IssInstallDeleteDefinitionElement(ASTNode node) {
        super(node, IssInstallDeleteSectionElement.class);
    }

    @NotNull
    @Override
    public IssInstallDeleteProperty[] getPropertyTypeList() {
        return IssInstallDeleteProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.InstallDelete;
    }

    @Nullable
    @Override
    public String getName() {
        return getInstallDeleteName() == null ? null : getInstallDeleteName().getPropertyValue() == null ? null : getInstallDeleteName().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyDeleteTypeElement getInstallDeleteType() {
        return IssFindUtils.findProperty(this, IssInstallDeleteProperty.Type, IssPropertyDeleteTypeElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallDeleteName() {
        return IssFindUtils.findProperty(this, IssInstallDeleteProperty.Name, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getInstallDeleteComponentReference() {
        return IssFindUtils.findProperty(this, IssInstallDeleteProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getInstallDeleteTaskReference() {
        return IssFindUtils.findProperty(this, IssInstallDeleteProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
