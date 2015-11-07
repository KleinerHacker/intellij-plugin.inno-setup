package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssUnknownElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionAppNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionAppVersionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.*;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyDescriptionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyDescriptionValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeSectionElement;

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

        final PsiElement componentSection = createForComponentSection(node);
        if (componentSection != null)
            return componentSection;

        final PsiElement typeSection = createForTypeSection(node);
        if (typeSection != null)
            return typeSection;

        //Default
        if (IssMarkerFactory.SECTION_TITLE.equals(node.getElementType())) {
            return new IssSectionNameElement(node);
        } else if (IssMarkerFactory.IDENTIFIER.equals(node.getElementType())) {
            return new IssIdentifierElement(node);
        } else if (IssMarkerFactory.VALUE.equals(node.getElementType())) {
            return new IssValueElement(node);
        } else if (IssMarkerFactory.ITEM_DEFAULT.equals(node.getElementType())) {
            return new IssDefinitionPropertyDefaultElement(node);
        }

        return new IssUnknownElement(node);
    }

    private static PsiElement createForComponentSection(ASTNode node) {
        if (IssMarkerFactory.ComponentSection.SECTION.equals(node.getElementType())) {
            return new IssComponentSectionElement(node);
        } else if (IssMarkerFactory.ComponentSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssComponentDefinitionElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssComponentPropertyNameElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssComponentPropertyNameValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssComponentPropertyDescriptionElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssComponentPropertyDescriptionValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES.equals(node.getElementType())) {
            return new IssComponentPropertyTypesElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES_VALUE.equals(node.getElementType())) {
            return new IssComponentPropertyTypesValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssComponentPropertyFlagsElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssComponentPropertyFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForFileSection(ASTNode node) {
        if (IssMarkerFactory.FileSection.SECTION.equals(node.getElementType())) {
            return new IssFileSectionElement(node);
        } else if (IssMarkerFactory.FileSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssFileDefinitionElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE.equals(node.getElementType())) {
            return new IssFilePropertySourceElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE.equals(node.getElementType())) {
            return new IssFilePropertySourceValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR.equals(node.getElementType())) {
            return new IssFilePropertyDestDirElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyDestDirValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COPYMODE.equals(node.getElementType())) {
            return new IssFilePropertyCopyModeElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COPYMODE_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyCopyModeValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_ATTRIBUTE.equals(node.getElementType())) {
            return new IssFilePropertyAttributeElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_ATTRIBUTE_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyAttributeValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_PERMISSIONS.equals(node.getElementType())) {
            return new IssFilePropertyPermissionsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_PERMISSIONS_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyPermissionsValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS.equals(node.getElementType())) {
            return new IssFilePropertyTasksElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyTasksValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssFilePropertyComponentsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyComponentsValueElement(node);
        }else if (IssMarkerFactory.FileSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssFilePropertyFlagsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssFilePropertyFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTaskSection(ASTNode node) {
        if (IssMarkerFactory.TaskSection.SECTION.equals(node.getElementType())) {
            return new IssTaskSectionElement(node);
        } else if (IssMarkerFactory.TaskSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTaskDefinitionElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssTaskPropertyNameElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssTaskPropertyNameValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssTaskPropertyDescriptionElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssTaskPropertyDescriptionValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssTaskPropertyComponentsElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssTaskPropertyComponentsValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssTaskPropertyFlagsElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssTaskPropertyFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTypeSection(ASTNode node) {
        if (IssMarkerFactory.TypeSection.SECTION.equals(node.getElementType())) {
            return new IssTypeSectionElement(node);
        } else if (IssMarkerFactory.TypeSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTypeDefinitionElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssTypePropertyNameElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssTypePropertyNameValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssTypePropertyDescriptionElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssTypePropertyDescriptionValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssTypePropertyFlagsElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssTypePropertyFlagsValueElement(node);
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
