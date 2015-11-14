package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssUnknownElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.setup.IssSetupSectionAppNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.setup.IssSetupSectionAppVersionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.setup.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

/**
 * Created by Christoph on 14.12.2014.
 */
public final class IssPsiElementFactory {

    public static PsiElement create(ASTNode node) {
        final PsiElement commonSection = createForCommonSection(node);
        if (commonSection != null)
            return commonSection;

        final PsiElement setupSection = createForSetupSection(node);
        if (setupSection != null)
            return setupSection;

        final PsiElement taskSection = createForTaskSection(node);
        if (taskSection != null)
            return taskSection;

        final PsiElement fileSection = createForFileSection(node);
        if (fileSection != null)
            return fileSection;

        final PsiElement directorySection = createForDirectorySection(node);
        if (directorySection != null)
            return directorySection;

        final PsiElement componentSection = createForComponentSection(node);
        if (componentSection != null)
            return componentSection;

        final PsiElement typeSection = createForTypeSection(node);
        if (typeSection != null)
            return typeSection;

        final PsiElement iconSection = createForIconSection(node);
        if (iconSection != null)
            return iconSection;

        final PsiElement runSection = createForRunSection(node);
        if (runSection != null)
            return runSection;

        //Default
        if (IssMarkerFactory.SECTION_TITLE.equals(node.getElementType())) {
            return new IssSectionNameElement(node);
        } else if (IssMarkerFactory.IDENTIFIER.equals(node.getElementType())) {
            return new IssIdentifierElement(node);
        } else if (IssMarkerFactory.VALUE.equals(node.getElementType())) {
            return new IssValueElement(node);
        } else if (IssMarkerFactory.PROPERTY_UNKNOWN.equals(node.getElementType())) {
            return new IssPropertyUnknownElement(node);
        }

        return new IssUnknownElement(node);
    }

    private static PsiElement createForIconSection(ASTNode node) {
        if (IssMarkerFactory.IconSection.SECTION.equals(node.getElementType())) {
            return new IssIconSectionElement(node);
        } else if (IssMarkerFactory.IconSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssIconDefinitionElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Name);
        } else if (IssMarkerFactory.IconSection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyIconFlagsElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIconFlagsValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Filename);
        } else if (IssMarkerFactory.IconSection.PROPERTY_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_APPUSERMODELID.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.AppUserModelID);
        } else if (IssMarkerFactory.IconSection.PROPERTY_APPUSERMODELID_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_COMMENT.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Comment);
        } else if (IssMarkerFactory.IconSection.PROPERTY_COMMENT_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_HOTKEY.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.HotKey);
        } else if (IssMarkerFactory.IconSection.PROPERTY_HOTKEY_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_ICONFILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.IconFilename);
        } else if (IssMarkerFactory.IconSection.PROPERTY_ICONFILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_ICONINDEX.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssIconProperty.IconIndex);
        } else if (IssMarkerFactory.IconSection.PROPERTY_ICONINDEX_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_PARAMETERS.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Parameters);
        } else if (IssMarkerFactory.IconSection.PROPERTY_PARAMETERS_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.PROPERTY_WORKINGDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.WorkingDirectory);
        } else if (IssMarkerFactory.IconSection.PROPERTY_WORKINGDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        }

        return null;
    }

    private static PsiElement createForRunSection(ASTNode node) {
        if (IssMarkerFactory.RunSection.SECTION.equals(node.getElementType())) {
            return new IssRunSectionElement(node);
        } else if (IssMarkerFactory.RunSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssRunDefinitionElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.Filename);
        } else if (IssMarkerFactory.RunSection.PROPERTY_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.Description);
        } else if (IssMarkerFactory.RunSection.PROPERTY_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_PARAMETERS.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.Parameters);
        } else if (IssMarkerFactory.RunSection.PROPERTY_PARAMETERS_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_WORKINGDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.WorkingDirectory);
        } else if (IssMarkerFactory.RunSection.PROPERTY_WORKINGDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_STATUSMSG.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.StatusMessage);
        } else if (IssMarkerFactory.RunSection.PROPERTY_STATUSMSG_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_RUNONCEID.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.RunOnceId);
        } else if (IssMarkerFactory.RunSection.PROPERTY_RUNONCEID_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_VERB.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRunProperty.Verb);
        } else if (IssMarkerFactory.RunSection.PROPERTY_VERB_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyRunFlagsElement(node);
        } else if (IssMarkerFactory.RunSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyRunFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForComponentSection(ASTNode node) {
        if (IssMarkerFactory.ComponentSection.SECTION.equals(node.getElementType())) {
            return new IssComponentSectionElement(node);
        } else if (IssMarkerFactory.ComponentSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssComponentDefinitionElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssComponentProperty.Name);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssComponentProperty.Description);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_EXTRADISKSPACEREQUIRED.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssComponentProperty.ExtraDiskSpaceRequired);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_EXTRADISKSPACEREQUIRED_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_TYPES.equals(node.getElementType())) {
            return new IssPropertyTypeReferenceElement(node, IssComponentProperty.TypeReference);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_TYPES_VALUE.equals(node.getElementType())) {
            return new IssPropertyTypeReferenceValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyComponentFlagsElement(node);
        } else if (IssMarkerFactory.ComponentSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForFileSection(ASTNode node) {
        if (IssMarkerFactory.FileSection.SECTION.equals(node.getElementType())) {
            return new IssFileSectionElement(node);
        } else if (IssMarkerFactory.FileSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssFileDefinitionElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_SOURCE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.Source);
        } else if (IssMarkerFactory.FileSection.PROPERTY_SOURCE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_DESTDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.DestinationDirectory);
        } else if (IssMarkerFactory.FileSection.PROPERTY_DESTDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_COPYMODE.equals(node.getElementType())) {
            return new IssPropertyIOCopyModeElement(node, IssFileProperty.CopyMode);
        } else if (IssMarkerFactory.FileSection.PROPERTY_COPYMODE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOCopyModeValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_ATTRIBUTE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeElement(node, IssFileProperty.Attributes);
        } else if (IssMarkerFactory.FileSection.PROPERTY_ATTRIBUTE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_PERMISSIONS.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsElement(node, IssFileProperty.Permissions);
        } else if (IssMarkerFactory.FileSection.PROPERTY_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_DESTNAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.DestinationName);
        } else if (IssMarkerFactory.FileSection.PROPERTY_DESTNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_EXCLUDES.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.Excludes);
        } else if (IssMarkerFactory.FileSection.PROPERTY_EXCLUDES_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_EXTERNALSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssFileProperty.ExternalSize);
        } else if (IssMarkerFactory.FileSection.PROPERTY_EXTERNALSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_FONTINSTALL.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.FontInstall);
        } else if (IssMarkerFactory.FileSection.PROPERTY_FONTINSTALL_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_STRONGASSEMBLYNAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.StrongAssemblyName);
        } else if (IssMarkerFactory.FileSection.PROPERTY_STRONGASSEMBLYNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyFileFlagsElement(node);
        } else if (IssMarkerFactory.FileSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyFileFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForDirectorySection(ASTNode node) {
        if (IssMarkerFactory.DirectorySection.SECTION.equals(node.getElementType())) {
            return new IssDirectorySectionElement(node);
        } else if (IssMarkerFactory.DirectorySection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssDirectoryDefinitionElement(node);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssDirectoryProperty.Name);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeElement(node, IssDirectoryProperty.Attributes);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_ATTRIBUTE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsElement(node, IssDirectoryProperty.Permissions);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyDirectoryFlagsElement(node);
        } else if (IssMarkerFactory.DirectorySection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyDirectoryFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTaskSection(ASTNode node) {
        if (IssMarkerFactory.TaskSection.SECTION.equals(node.getElementType())) {
            return new IssTaskSectionElement(node);
        } else if (IssMarkerFactory.TaskSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTaskDefinitionElement(node);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssTaskProperty.Name);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTaskProperty.Description);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_GROUPDESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTaskProperty.GroupDescription);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_GROUPDESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyTaskFlagsElement(node);
        } else if (IssMarkerFactory.TaskSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTypeSection(ASTNode node) {
        if (IssMarkerFactory.TypeSection.SECTION.equals(node.getElementType())) {
            return new IssTypeSectionElement(node);
        } else if (IssMarkerFactory.TypeSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTypeDefinitionElement(node);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssTypeProperty.Name);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTypeProperty.Description);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyTypeFlagsElement(node);
        } else if (IssMarkerFactory.TypeSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTypeFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForSetupSection(ASTNode node) {
        if (IssMarkerFactory.SetupSection.SECTION.equals(node.getElementType())) {
            return new IssSetupSectionElement(node);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APP_NAME.equals(node.getElementType())) {
            return new IssSetupSectionAppNameElement(node);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APP_VERSION.equals(node.getElementType())) {
            return new IssSetupSectionAppVersionElement(node);
        }

        return null;
    }

    private static PsiElement createForCommonSection(ASTNode node) {
        if (IssMarkerFactory.CommonSection.PROPERTY_TASKS.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceElement(node, IssCommonProperty.TaskReference);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_TASKS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceValueElement(node);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_COMPONENTS.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceElement(node, IssCommonProperty.ComponentReference);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceValueElement(node);
        }

        return null;
    }

    private IssPsiElementFactory() {
    }
}
