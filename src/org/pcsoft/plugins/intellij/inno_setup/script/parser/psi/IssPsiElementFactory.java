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

        //Default
        if (IssMarkerFactory.SECTION_TITLE.equals(node.getElementType())) {
            return new IssSectionNameElement(node);
        } else if (IssMarkerFactory.IDENTIFIER.equals(node.getElementType())) {
            return new IssIdentifierElement(node);
        } else if (IssMarkerFactory.VALUE.equals(node.getElementType())) {
            return new IssValueElement(node);
        } else if (IssMarkerFactory.ITEM_UNKNOWN.equals(node.getElementType())) {
            return new IssPropertyUnknownElement(node);
        }

        return new IssUnknownElement(node);
    }

    private static PsiElement createForIconSection(ASTNode node) {
        if (IssMarkerFactory.IconSection.SECTION.equals(node.getElementType())) {
            return new IssIconSectionElement(node);
        } else if (IssMarkerFactory.IconSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssIconDefinitionElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Name);
        } else if (IssMarkerFactory.IconSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyIconFlagsElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIconFlagsValueElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_FILENAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssIconProperty.Filename);
        } else if (IssMarkerFactory.IconSection.ITEM_FILENAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_TASKS.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceElement(node, IssIconProperty.TaskReference);
        } else if (IssMarkerFactory.IconSection.ITEM_TASKS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceValueElement(node);
        } else if (IssMarkerFactory.IconSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceElement(node, IssIconProperty.ComponentReference);
        } else if (IssMarkerFactory.IconSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceValueElement(node);
        }


        return null;
    }

    private static PsiElement createForComponentSection(ASTNode node) {
        if (IssMarkerFactory.ComponentSection.SECTION.equals(node.getElementType())) {
            return new IssComponentSectionElement(node);
        } else if (IssMarkerFactory.ComponentSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssComponentDefinitionElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssComponentProperty.Name);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssComponentProperty.Description);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_EXTRADISKSPACEREQUIRED.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssComponentProperty.ExtraDiskSpaceRequired);
        } else if (IssMarkerFactory.ComponentSection.ITEM_EXTRADISKSPACEREQUIRED_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES.equals(node.getElementType())) {
            return new IssPropertyTypeReferenceElement(node, IssComponentProperty.TypeReference);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES_VALUE.equals(node.getElementType())) {
            return new IssPropertyTypeReferenceValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyComponentFlagsElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForFileSection(ASTNode node) {
        if (IssMarkerFactory.FileSection.SECTION.equals(node.getElementType())) {
            return new IssFileSectionElement(node);
        } else if (IssMarkerFactory.FileSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssFileDefinitionElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.Source);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.DestinationDirectory);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COPYMODE.equals(node.getElementType())) {
            return new IssPropertyIOCopyModeElement(node, IssFileProperty.CopyMode);
        } else if (IssMarkerFactory.FileSection.ITEM_COPYMODE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOCopyModeValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_ATTRIBUTE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeElement(node, IssFileProperty.Attributes);
        } else if (IssMarkerFactory.FileSection.ITEM_ATTRIBUTE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_PERMISSIONS.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsElement(node, IssFileProperty.Permissions);
        } else if (IssMarkerFactory.FileSection.ITEM_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTNAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.DestinationName);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_EXCLUDES.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.Excludes);
        } else if (IssMarkerFactory.FileSection.ITEM_EXCLUDES_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_EXTERNALSIZE.equals(node.getElementType())) {
            return new IssPropertyIntegerElement(node, IssFileProperty.ExternalSize);
        } else if (IssMarkerFactory.FileSection.ITEM_EXTERNALSIZE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIntegerValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_FONTINSTALL.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.FontInstall);
        } else if (IssMarkerFactory.FileSection.ITEM_FONTINSTALL_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_STRONGASSEMBLYNAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssFileProperty.StrongAssemblyName);
        } else if (IssMarkerFactory.FileSection.ITEM_STRONGASSEMBLYNAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceElement(node, IssFileProperty.TaskReference);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceElement(node, IssFileProperty.ComponentReference);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyFileFlagsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyFileFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForDirectorySection(ASTNode node) {
        if (IssMarkerFactory.DirectorySection.SECTION.equals(node.getElementType())) {
            return new IssDirectorySectionElement(node);
        } else if (IssMarkerFactory.DirectorySection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssDirectoryDefinitionElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_NAME.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssDirectoryProperty.Name);
        } else if (IssMarkerFactory.DirectorySection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_ATTRIBUTE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeElement(node, IssDirectoryProperty.Attributes);
        } else if (IssMarkerFactory.DirectorySection.ITEM_ATTRIBUTE_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOAttributeValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_PERMISSIONS.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsElement(node, IssDirectoryProperty.Permissions);
        } else if (IssMarkerFactory.DirectorySection.ITEM_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssPropertyIOPermissionsValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_TASKS.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceElement(node, IssDirectoryProperty.TaskReference);
        } else if (IssMarkerFactory.DirectorySection.ITEM_TASKS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskReferenceValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceElement(node, IssDirectoryProperty.ComponentReference);
        } else if (IssMarkerFactory.DirectorySection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceValueElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyDirectoryFlagsElement(node);
        } else if (IssMarkerFactory.DirectorySection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyDirectoryFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTaskSection(ASTNode node) {
        if (IssMarkerFactory.TaskSection.SECTION.equals(node.getElementType())) {
            return new IssTaskSectionElement(node);
        } else if (IssMarkerFactory.TaskSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTaskDefinitionElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssTaskProperty.Name);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTaskProperty.Description);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_GROUPDESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTaskProperty.GroupDescription);
        } else if (IssMarkerFactory.TaskSection.ITEM_GROUPDESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceElement(node, IssTaskProperty.ComponentReference);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssPropertyComponentReferenceValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyTaskFlagsElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTaskFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTypeSection(ASTNode node) {
        if (IssMarkerFactory.TypeSection.SECTION.equals(node.getElementType())) {
            return new IssTypeSectionElement(node);
        } else if (IssMarkerFactory.TypeSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTypeDefinitionElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssPropertyNameElement(node, IssTypeProperty.Name);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssPropertyNameValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssPropertyStringElement(node, IssTypeProperty.Description);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssPropertyStringValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssPropertyTypeFlagsElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssPropertyTypeFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForSetupSection(ASTNode node) {
        if (IssMarkerFactory.SetupSection.SECTION.equals(node.getElementType())) {
            return new IssSetupSectionElement(node);
        } else if (IssMarkerFactory.SetupSection.ITEM_APP_NAME.equals(node.getElementType())) {
            return new IssSetupSectionAppNameElement(node);
        } else if (IssMarkerFactory.SetupSection.ITEM_APP_VERSION.equals(node.getElementType())) {
            return new IssSetupSectionAppVersionElement(node);
        }

        return null;
    }

    private IssPsiElementFactory() {
    }
}
