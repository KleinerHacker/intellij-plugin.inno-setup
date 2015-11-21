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
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssInstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssInstallRunDefinitionElement extends IssDefinitionElement<IssInstallRunSectionElement, IssInstallRunProperty> {

    public IssInstallRunDefinitionElement(ASTNode node) {
        super(node, IssInstallRunSectionElement.class);
    }

    @NotNull
    @Override
    public IssInstallRunProperty[] getPropertyTypeList() {
        return IssInstallRunProperty.values();
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
                return IssIcons.IC_SECT_INSTALL_RUN;
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
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunDescription() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunParameters() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Parameters, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunWorkingDirectory() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.WorkingDirectory, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunStatusMessage() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getRunVerb() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Verb, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyInstallRunFlagsElement getRunFlags() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Flags, IssPropertyInstallRunFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getRunComponentReference() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getRunTaskReference() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
