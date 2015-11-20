package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileDefinitionElement extends IssDefinitionElement<IssFileSectionElement, IssFileProperty> {

    public IssFileDefinitionElement(ASTNode node) {
        super(node, IssFileSectionElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty[] getPropertyTypeList() {
        return IssFileProperty.values();
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
                return "Files";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_FILE;
            }
        };
    }

    @Nullable
    @Override
    public String getName() {
        return getFileSource() == null ? null : getFileSource().getPropertyValue() == null ? null : getFileSource().getPropertyValue().getString();
    }

    @Nullable
    public IssPropertyStringElement getFileSource() {
        return IssFindUtils.findProperty(this, IssFileProperty.Source, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getFileDestDir() {
        return IssFindUtils.findProperty(this, IssFileProperty.DestinationDirectory, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getFileDestName() {
        return IssFindUtils.findProperty(this, IssFileProperty.DestinationName, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getFileExcludes() {
        return IssFindUtils.findProperty(this, IssFileProperty.Excludes, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyIntegerElement getFileExternalSize() {
        return IssFindUtils.findProperty(this, IssFileProperty.ExternalSize, IssPropertyIntegerElement.class);
    }

    @Nullable
    public IssPropertyStringElement getFileFontInstall() {
        return IssFindUtils.findProperty(this, IssFileProperty.FontInstall, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyStringElement getFileStrongAssemblyName() {
        return IssFindUtils.findProperty(this, IssFileProperty.StrongAssemblyName, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyFileFlagsElement getFileFlags() {
        return IssFindUtils.findProperty(this, IssFileProperty.Flags, IssPropertyFileFlagsElement.class);
    }

    @Nullable
    public IssPropertyIOCopyModeElement getFileCopyMode() {
        return IssFindUtils.findProperty(this, IssFileProperty.CopyMode, IssPropertyIOCopyModeElement.class);
    }

    @Nullable
    public IssPropertyIOAttributeElement getFileAttribute() {
        return IssFindUtils.findProperty(this, IssFileProperty.Attributes, IssPropertyIOAttributeElement.class);
    }

    @Nullable
    public IssPropertyIOPermissionsElement getFilePermissions() {
        return IssFindUtils.findProperty(this, IssFileProperty.Permissions, IssPropertyIOPermissionsElement.class);
    }

    @Nullable
    public IssPropertyComponentReferenceElement getFileComponentReference() {
        return IssFindUtils.findProperty(this, IssFileProperty.ComponentReference, IssPropertyComponentReferenceElement.class);
    }

    @Nullable
    public IssPropertyTaskReferenceElement getFileTaskReference() {
        return IssFindUtils.findProperty(this, IssFileProperty.TaskReference, IssPropertyTaskReferenceElement.class);
    }
}
