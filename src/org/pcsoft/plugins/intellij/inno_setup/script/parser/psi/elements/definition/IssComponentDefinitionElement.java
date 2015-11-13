package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssComponentSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

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
                return "Components";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_COMPONENT;
            }
        };
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
