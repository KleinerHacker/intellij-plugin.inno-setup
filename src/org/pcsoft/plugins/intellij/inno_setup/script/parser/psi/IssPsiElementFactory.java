package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssUnknownElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionDescriptionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionDescriptionValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionTypesElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionTypesValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionComponentsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionDestDirElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionDestDirValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionSourceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionSourceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionTasksElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionTasksValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionAppNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionAppVersionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionComponentsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionDescriptionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionDescriptionValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionDescriptionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionDescriptionValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionNameValueElement;
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
            return new IssDefinitionItemDefaultElement(node);
        }

        return new IssUnknownElement(node);
    }

    private static PsiElement createForComponentSection(ASTNode node) {
        if (IssMarkerFactory.ComponentSection.SECTION.equals(node.getElementType())) {
            return new IssComponentSectionElement(node);
        } else if (IssMarkerFactory.ComponentSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssComponentDefinitionElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssComponentDefinitionNameElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssComponentDefinitionNameValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssComponentDefinitionDescriptionElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssComponentDefinitionDescriptionValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES.equals(node.getElementType())) {
            return new IssComponentDefinitionTypesElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_TYPES_VALUE.equals(node.getElementType())) {
            return new IssComponentDefinitionTypesValueElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssComponentDefinitionFlagsElement(node);
        } else if (IssMarkerFactory.ComponentSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssComponentDefinitionFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForFileSection(ASTNode node) {
        if (IssMarkerFactory.FileSection.SECTION.equals(node.getElementType())) {
            return new IssFileSectionElement(node);
        } else if (IssMarkerFactory.FileSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssFileDefinitionElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE.equals(node.getElementType())) {
            return new IssFileDefinitionSourceElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_SOURCE_VALUE.equals(node.getElementType())) {
            return new IssFileDefinitionSourceValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR.equals(node.getElementType())) {
            return new IssFileDefinitionDestDirElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_DESTDIR_VALUE.equals(node.getElementType())) {
            return new IssFileDefinitionDestDirValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS.equals(node.getElementType())) {
            return new IssFileDefinitionTasksElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_TASKS_VALUE.equals(node.getElementType())) {
            return new IssFileDefinitionTasksValueElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssFileDefinitionComponentsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssFileDefinitionComponentsValueElement(node);
        }else if (IssMarkerFactory.FileSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssFileDefinitionFlagsElement(node);
        } else if (IssMarkerFactory.FileSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssFileDefinitionFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTaskSection(ASTNode node) {
        if (IssMarkerFactory.TaskSection.SECTION.equals(node.getElementType())) {
            return new IssTaskSectionElement(node);
        } else if (IssMarkerFactory.TaskSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTaskDefinitionElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssTaskDefinitionNameElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssTaskDefinitionNameValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssTaskDefinitionDescriptionElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssTaskDefinitionDescriptionValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS.equals(node.getElementType())) {
            return new IssTaskDefinitionComponentsElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_COMPONENTS_VALUE.equals(node.getElementType())) {
            return new IssTaskDefinitionComponentsValueElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssTaskDefinitionFlagsElement(node);
        } else if (IssMarkerFactory.TaskSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssTaskDefinitionFlagsValueElement(node);
        }

        return null;
    }

    private static PsiElement createForTypeSection(ASTNode node) {
        if (IssMarkerFactory.TypeSection.SECTION.equals(node.getElementType())) {
            return new IssTypeSectionElement(node);
        } else if (IssMarkerFactory.TypeSection.SECTION_DEFINITION.equals(node.getElementType())) {
            return new IssTypeDefinitionElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME.equals(node.getElementType())) {
            return new IssTypeDefinitionNameElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_NAME_VALUE.equals(node.getElementType())) {
            return new IssTypeDefinitionNameValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION.equals(node.getElementType())) {
            return new IssTypeDefinitionDescriptionElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_DESCRIPTION_VALUE.equals(node.getElementType())) {
            return new IssTypeDefinitionDescriptionValueElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS.equals(node.getElementType())) {
            return new IssTypeDefinitionFlagsElement(node);
        } else if (IssMarkerFactory.TypeSection.ITEM_FLAGS_VALUE.equals(node.getElementType())) {
            return new IssTypeDefinitionFlagsValueElement(node);
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
