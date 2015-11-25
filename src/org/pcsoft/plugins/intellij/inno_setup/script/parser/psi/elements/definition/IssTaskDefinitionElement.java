package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Task;
    }

    @Nullable
    @Override
    public String getName() {
        return getTaskName() == null ? null : getTaskName().getPropertyValue() == null ? null : getTaskName().getPropertyValue().getName();
    }

    @Nullable
    public IssPropertyNameElement getTaskName() {
        return IssFindUtils.findProperty(this, IssTaskProperty.Name, IssPropertyNameElement.class);
    }

    @Nullable
    public IssPropertyStringElement getTaskDescription() {
        return IssFindUtils.findProperty(this, IssTaskProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getTaskGroupDescription() {
        return IssFindUtils.findProperty(this, IssTaskProperty.GroupDescription, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getTaskComponentReference() {
        return IssFindUtils.findProperty(this, IssTaskProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskFlagsElement getTaskFlags() {
        return IssFindUtils.findProperty(this, IssTaskProperty.Flags, IssPropertyTaskFlagsElement.class);
    }
}
