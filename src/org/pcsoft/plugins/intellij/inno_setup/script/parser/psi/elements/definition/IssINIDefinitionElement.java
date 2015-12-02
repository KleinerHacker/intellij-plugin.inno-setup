package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyINIFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssINISectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssINIProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.INI;
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
