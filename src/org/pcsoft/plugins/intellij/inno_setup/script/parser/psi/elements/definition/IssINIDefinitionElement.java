package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyINIFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssINISectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssINIProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssINIDefinitionElement extends IssDefinitionElement<IssINISectionElement, IssINIProperty> {

    public IssINIDefinitionElement(ASTNode node) {
        super(node, IssINISectionElement.class);
    }

    @NotNull
    @Override
    public IssINIProperty[] getPropertyTypeList() {
        return IssINIProperty.values();
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
                return "INI";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_INI;
            }
        };
    }

    @Nullable
    @Override
    public String getName() {
        return getINIFilename() == null ? null : getINIFilename().getPropertyValue() == null ? null : getINIFilename().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getINIFilename() {
        return IssFindUtils.findProperty(this, IssINIProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getINISection() {
        return IssFindUtils.findProperty(this, IssINIProperty.Section, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getINIKey() {
        return IssFindUtils.findProperty(this, IssINIProperty.Key, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getINIString() {
        return IssFindUtils.findProperty(this, IssINIProperty.String, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyINIFlagsElement getINIFlags() {
        return IssFindUtils.findProperty(this, IssINIProperty.Flags, IssPropertyINIFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getINIComponentReference() {
        return IssFindUtils.findProperty(this, IssINIProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getINITaskReference() {
        return IssFindUtils.findProperty(this, IssINIProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
