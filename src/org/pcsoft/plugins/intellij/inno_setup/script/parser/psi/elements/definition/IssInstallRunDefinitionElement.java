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
        return getInstallRunFilename() == null ? null : getInstallRunFilename().getPropertyValue() == null ? null : getInstallRunFilename().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getInstallRunFilename() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallRunDescription() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallRunParameters() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Parameters, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallRunWorkingDirectory() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.WorkingDirectory, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallRunStatusMessage() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Filename, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getInstallRunVerb() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Verb, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyInstallRunFlagsElement getInstallRunFlags() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.Flags, IssPropertyInstallRunFlagsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getInstallRunComponentReference() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getInstallRunTaskReference() {
        return IssFindUtils.findProperty(this, IssInstallRunProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
