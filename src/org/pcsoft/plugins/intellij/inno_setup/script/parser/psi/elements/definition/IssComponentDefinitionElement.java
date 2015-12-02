package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIntegerElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssComponentSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentDefinitionElement extends IssDefinitionElement<IssComponentSectionElement, IssComponentProperty> {

    public IssComponentDefinitionElement(ASTNode node) {
        super(node, IssComponentSectionElement.class);
    }

    @NotNull
    @Override
    public IssComponentProperty[] getPropertyTypeList() {
        return IssComponentProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Component;
    }

    @Nullable
    @Override
    public String getName() {
        return getComponentName() == null ? null : getComponentName().getPropertyValue() == null ? null : getComponentName().getPropertyValue().getName();
    }

    @Nullable
    public IssPropertyNameElement getComponentName() {
        return IssFindUtils.findProperty(this, IssComponentProperty.Name, IssPropertyNameElement.class);
    }

    @Nullable
    public IssPropertyStringElement getComponentDescription() {
        return IssFindUtils.findProperty(this, IssComponentProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyIntegerElement getComponentExtraDiskSpaceRequired() {
        return IssFindUtils.findProperty(this, IssComponentProperty.ExtraDiskSpaceRequired, IssPropertyIntegerElement.class);
    }

    @Nullable
    public IssPropertyComponentFlagsElement getComponentFlags() {
        return IssFindUtils.findProperty(this, IssComponentProperty.Flags, IssPropertyComponentFlagsElement.class);
    }

    @Nullable
    public IssPropertyTypeReferenceElement getComponentTypeReference() {
        return IssFindUtils.findProperty(this, IssComponentProperty.TypeReference, IssPropertyTypeReferenceElement.class);
    }
}
