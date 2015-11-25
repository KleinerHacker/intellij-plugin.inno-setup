package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIconFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Icon;
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
