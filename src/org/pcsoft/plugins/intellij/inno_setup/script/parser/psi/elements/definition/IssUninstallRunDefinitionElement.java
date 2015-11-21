package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyInstallRunFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssUninstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssUninstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssUninstallRunDefinitionElement extends IssDefinitionElement<IssUninstallRunSectionElement, IssUninstallRunProperty> {

    public IssUninstallRunDefinitionElement(ASTNode node) {
        super(node, IssUninstallRunSectionElement.class);
    }

    @NotNull
    @Override
    public IssUninstallRunProperty[] getPropertyTypeList() {
        return IssUninstallRunProperty.values();
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
                return "UninstallRun";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_UNINSTALL_RUN;
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
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunParameters() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.Parameters, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunWorkingDirectory() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.WorkingDirectory, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunOnceId() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.RunOnceId, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunVerb() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.Verb, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyInstallRunFlagsElement getRunFlags() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.Flags, IssPropertyInstallRunFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getRunComponentReference() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getRunTaskReference() {
        return IssFindUtils.findProperty(this, IssUninstallRunProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}