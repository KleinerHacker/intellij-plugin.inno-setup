package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssUnknownElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.*;
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

        final PsiElement installRunSection = createForInstallRunSection(node);
        if (installRunSection != null)
            return installRunSection;

        final PsiElement uninstallRunSection = createForUninstallRunSection(node);
        if (uninstallRunSection != null)
            return uninstallRunSection;

        final PsiElement iniSection = createForINISection(node);
        if (iniSection != null)
            return iniSection;

        final PsiElement registrySection = createForRegistrySection(node);
        if (registrySection != null)
            return registrySection;

        final PsiElement customMessageSection = createForCustomMessageSection(node);
        if (customMessageSection != null)
            return customMessageSection;

        final PsiElement messageSection = createForMessageSection(node);
        if (messageSection != null)
            return messageSection;

        final PsiElement languageOptionSection = createForLanguageOptionSection(node);
        if (languageOptionSection != null)
            return languageOptionSection;

        final PsiElement languageSection = createForLanguageSection(node);
        if (languageSection != null)
            return languageSection;

        //Default
        if (IssMarkerFactory.SECTION_TITLE.equals(node.getElementType())) {
            return new IssSectionNameElement(node);
        } else if (IssMarkerFactory.IDENTIFIER.equals(node.getElementType())) {
            return new IssIdentifierElement(node);
        } else if (IssMarkerFactory.IDENTIFIER_NAME.equals(node.getElementType())) {
            return new IssIdentifierNameElement(node);
        } else if (IssMarkerFactory.IDENTIFIER_REFERENCE.equals(node.getElementType())) {
            return new IssIdentifierReferenceElement(node);
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

    private static PsiElement createForInstallRunSection(ASTNode node) {
        if (IssMarkerFactory.InstallRunSection.SECTION.equals(node.getElementType())) {
            return new IssInstallRunSectionElement(node);
        } else if (IssMarkerFactory.InstallRunSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssInstallRunDefinitionElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.Filename);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.Description);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_PARAMETERS.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.Parameters);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_PARAMETERS_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_WORKINGDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.WorkingDirectory);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_WORKINGDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_STATUSMSG.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.StatusMessage);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_STATUSMSG_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_VERB.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssInstallRunProperty.Verb);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_VERB_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyInstallRunFlagsElement(node);
        } else if (IssMarkerFactory.InstallRunSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyInstallRunFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForUninstallRunSection(ASTNode node) {
        if (IssMarkerFactory.UninstallRunSection.SECTION.equals(node.getElementType())) {
            return new IssUninstallRunSectionElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssUninstallRunDefinitionElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssUninstallRunProperty.Filename);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_PARAMETERS.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssUninstallRunProperty.Parameters);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_PARAMETERS_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_WORKINGDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssUninstallRunProperty.WorkingDirectory);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_WORKINGDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_RUNONCEID.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssUninstallRunProperty.RunOnceId);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_RUNONCEID_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_VERB.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssUninstallRunProperty.Verb);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_VERB_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyUninstallRunFlagsElement(node);
        } else if (IssMarkerFactory.UninstallRunSection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyUninstallRunFlagsValueElement(node);
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

    private static PsiElement createForLanguageSection(ASTNode node) {
        if (IssMarkerFactory.LanguageSection.SECTION.equals(node.getElementType())) {
            return new IssLanguageSectionElement(node);
        } else if (IssMarkerFactory.LanguageSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssLanguageDefinitionElement(node);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssLanguageProperty.Name);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_MEESAGEFILE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssLanguageProperty.MessageFile);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_MEESAGEFILE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_LICENCEFILE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssLanguageProperty.LicenceFile);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_LICENCEFILE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        }else if (IssMarkerFactory.LanguageSection.PROPERTY_INFOBEFOREFILE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssLanguageProperty.InfoBeforeFile);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_INFOBEFOREFILE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        }else if (IssMarkerFactory.LanguageSection.PROPERTY_INFOAFTERFILE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssLanguageProperty.InfoAfterFile);
        } else if (IssMarkerFactory.LanguageSection.PROPERTY_INFOAFTERFILE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        }

        return null;
    }

    private static PsiElement createForINISection(ASTNode node) {
        if (IssMarkerFactory.INISection.SECTION.equals(node.getElementType())) {
            return new IssINISectionElement(node);
        } else if (IssMarkerFactory.INISection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssINIDefinitionElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssINIProperty.Filename);
        } else if (IssMarkerFactory.INISection.PROPERTY_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_SECTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssINIProperty.Section);
        } else if (IssMarkerFactory.INISection.PROPERTY_SECTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_KEY.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssINIProperty.Key);
        } else if (IssMarkerFactory.INISection.PROPERTY_KEY_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_STRING.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssINIProperty.String);
        } else if (IssMarkerFactory.INISection.PROPERTY_STRING_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyINIFlagsElement(node);
        } else if (IssMarkerFactory.INISection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyINIFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForRegistrySection(ASTNode node) {
        if (IssMarkerFactory.RegistrySection.SECTION.equals(node.getElementType())) {
            return new IssRegistrySectionElement(node);
        } else if (IssMarkerFactory.RegistrySection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssRegistryDefinitionElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_ROOT.equals(node.getElementType())) {
            return new IssPropertyRegistryRootElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_ROOT_VALUE.equals(node.getElementType())) {
            return new IssPropertyRegistryRootValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_SUBKEY.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRegistryProperty.Subkey);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_SUBKEY_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUETYPE.equals(node.getElementType())) {
            return new IssPropertyRegistryValueTypeElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUETYPE_VALUE.equals(node.getElementType())) {
            return new IssPropertyRegistryValueTypeValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRegistryProperty.ValueName);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUEDATA.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssRegistryProperty.ValueData);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_VALUEDATA_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_PERMISSIONS.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsElement(node, IssRegistryProperty.Permissions);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsValueElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_FLAGS.equals(node.getElementType())) {
            return new IssPropertyRegistryFlagsElement(node);
        } else if (IssMarkerFactory.RegistrySection.PROPERTY_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyRegistryFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForSetupSection(ASTNode node) {
        if (IssMarkerFactory.SetupSection.SECTION.equals(node.getElementType())) {
            return new IssSetupSectionElement(node);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APPNAME.equals(node.getElementType())) {
            return new IssPropertyDefaultElement(node, IssSetupProperty.AppName);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APPNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APPVERSION.equals(node.getElementType())) {
            return new IssPropertyDefaultElement(node, IssSetupProperty.AppVersion);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_APPVERSION_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_COMPRESSION.equals(node.getElementType())) {
            return new IssPropertyCompressionElement(node, IssSetupProperty.Compression);
        } else if (IssMarkerFactory.SetupSection.PROPERTY_COMPRESSION_VALUE.equals(node.getElementType())) {
            return new IssPropertyCompressionValueElement(node);
        }

        return null;
    }

    private static PsiElement createForLanguageOptionSection(ASTNode node) {
        if (IssMarkerFactory.LanguageOptionSection.SECTION.equals(node.getElementType())) {
            return new IssLanguageOptionSectionElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGENAME.equals(node.getElementType())) { //TODO: Name element?
            return new IssPropertyDefaultElement(node, IssLanguageOptionProperty.LanguageName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGEID.equals(node.getElementType())) {
            return new IssPropertyDefaultElement(node, IssLanguageOptionProperty.LanguageID);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGEID_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGECODEPAGE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssLanguageOptionProperty.LanguageCodePage);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_LANGUAGECODEPAGE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontElement(node, IssLanguageOptionProperty.DialogFontName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyFontValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssLanguageOptionProperty.DialogFontSize);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_DIALOGFONTSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontElement(node, IssLanguageOptionProperty.TitleFontName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssLanguageOptionProperty.TitleFontSize);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_TITLEFONTSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontElement(node, IssLanguageOptionProperty.WelcomeFontName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssLanguageOptionProperty.WelcomeFontName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_WELCOMEFONTSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontElement(node, IssLanguageOptionProperty.CopyrightFontName);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTNAME.equals(node.getElementType())) {
            return new IssPropertyFontValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssLanguageOptionProperty.CopyrightFontSize);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_COPYRIGHTFONTSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_RIGHTTOLEFT.equals(node.getElementType())) {
            return new IssPropertyBooleanElement(node, IssLanguageOptionProperty.RightToLeft);
        } else if (IssMarkerFactory.LanguageOptionSection.PROPERTY_RIGHTTOLEFT_VALUE.equals(node.getElementType())) {
            return new IssPropertyBooleanValueElement(node);
        }

        return null;
    }

    private static PsiElement createForCustomMessageSection(ASTNode node) {
        if (IssMarkerFactory.CustomMessageSection.SECTION.equals(node.getElementType())) {
            return new IssCustomMessageSectionElement(node);
        } else if (IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultElement(node, new IssPropertyIdentifier() {
                @NotNull
                @Override
                public String getId() {
                    return "";
                }

                @NotNull
                @Override
                public String getDescriptionKey() {
                    return "";
                }

                @NotNull
                @Override
                public IElementType getPropertyMarkerElement() {
                    return IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE;
                }

                @Nullable
                @Override
                public IElementType getPropertyValueMarkerElement() {
                    return IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE_VALUE;
                }

                @Override
                public boolean isRequired() {
                    return false;
                }

                @Override
                public boolean isDeprecated() {
                    return false;
                }

                @NotNull
                @Override
                public IssValueType getValueType() {
                    return IssValueType.Unknown;
                }
            });
        } else if (IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
        }

        return null;
    }

    private static PsiElement createForMessageSection(ASTNode node) {
        if (IssMarkerFactory.MessageSection.SECTION.equals(node.getElementType())) {
            return new IssMessageSectionElement(node);
        } else if (IssMarkerFactory.MessageSection.PROPERTY_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultElement(node, new IssPropertyIdentifier() {
                @NotNull
                @Override
                public String getId() {
                    return "";
                }

                @NotNull
                @Override
                public String getDescriptionKey() {
                    return "";
                }

                @NotNull
                @Override
                public IElementType getPropertyMarkerElement() {
                    return IssMarkerFactory.MessageSection.PROPERTY_VALUE;
                }

                @Nullable
                @Override
                public IElementType getPropertyValueMarkerElement() {
                    return IssMarkerFactory.MessageSection.PROPERTY_VALUE_VALUE;
                }

                @Override
                public boolean isRequired() {
                    return false;
                }

                @Override
                public boolean isDeprecated() {
                    return false;
                }

                @NotNull
                @Override
                public IssValueType getValueType() {
                    return IssValueType.Unknown;
                }
            });
        } else if (IssMarkerFactory.MessageSection.PROPERTY_VALUE_VALUE.equals(node.getElementType())) {
            return new IssPropertyDefaultValueElement(node);
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
        } else if (IssMarkerFactory.CommonSection.PROPERTY_LANGUAGES.equals(node.getElementType())) {
            return new IssPropertyLanguagesReferenceElement(node, IssCommonProperty.Languages);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_LANGUAGES_VALUE.equals(node.getElementType())) {
            return new IssPropertyLanguagesReferenceValueElement(node);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_MINVERSION.equals(node.getElementType())) {
            return new IssPropertyWindowsVersionElement(node, IssCommonProperty.MinimalVersion);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_MINVERSION_VALUE.equals(node.getElementType())) {
            return new IssPropertyWindowsVersionValueElement(node);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_ONLYBELOWVERSION.equals(node.getElementType())) {
            return new IssPropertyWindowsVersionElement(node, IssCommonProperty.OnlyBelowVersion);
        } else if (IssMarkerFactory.CommonSection.PROPERTY_ONLYBELOWVERSION_VALUE.equals(node.getElementType())) {
            return new IssPropertyWindowsVersionValueElement(node);
        }

        return null;
    }

    private IssPsiElementFactory() {
    }
}
