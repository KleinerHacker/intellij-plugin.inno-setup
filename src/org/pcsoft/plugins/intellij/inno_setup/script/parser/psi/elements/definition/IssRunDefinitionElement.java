package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyRunFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssRunDefinitionElement extends IssDefinitionElement<IssRunSectionElement, IssRunProperty> {

    public IssRunDefinitionElement(ASTNode node) {
        super(node, IssRunSectionElement.class);
    }

    @NotNull
    @Override
    public IssRunProperty[] getPropertyTypeList() {
        return IssRunProperty.values();
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
                return "Run";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_RUN;
            }
        };
    }

    @Nullable
    @Override
    public String getName() {
        return getRunFilename() == null ? null : getRunFilename().getPropertyValue() == null ? null : getRunFilename().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getRunFilename() {
        return IssFindUtils.findProperty(this, IssRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunDescription() {
        return IssFindUtils.findProperty(this, IssRunProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunParameters() {
        return IssFindUtils.findProperty(this, IssRunProperty.Parameters, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunWorkingDirectory() {
        return IssFindUtils.findProperty(this, IssRunProperty.WorkingDirectory, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunStatusMessage() {
        return IssFindUtils.findProperty(this, IssRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunOnceId() {
        return IssFindUtils.findProperty(this, IssRunProperty.RunOnceId, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunVerb() {
        return IssFindUtils.findProperty(this, IssRunProperty.Verb, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyRunFlagsElement getRunFlags() {
        return IssFindUtils.findProperty(this, IssRunProperty.Flags, IssPropertyRunFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getRunComponentReference() {
        return IssFindUtils.findProperty(this, IssRunProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getRunTaskReference() {
        return IssFindUtils.findProperty(this, IssRunProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
