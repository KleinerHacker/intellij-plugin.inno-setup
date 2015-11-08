package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionFileAnnotator extends IssAbstractSectionAnnotator<IssFileDefinitionElement> {

    public IssSectionFileAnnotator() {
        super(IssFileDefinitionElement.class);
    }

    @Override
    protected void detectErrors(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForRequiredProperties(fileDefinitionElement, annotationHolder);
        checkForReferences(fileDefinitionElement, annotationHolder);
        checkForKnownValues(fileDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (fileDefinitionElement.getFileFlags() != null) {
            fileDefinitionElement.getFileFlags().getPropertyValueList().stream()
                    .filter(item -> IssFileFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
        if (fileDefinitionElement.getFileCopyMode() != null) {
            fileDefinitionElement.getFileCopyMode().getPropertyValueList().stream()
                    .filter(item -> IssFileCopyMode.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown copy mode"));
        }
        if (fileDefinitionElement.getFileAttribute() != null) {
            fileDefinitionElement.getFileAttribute().getPropertyValueList().stream()
                    .filter(item -> IssFileAttribute.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown attribute"));
        }
        if (fileDefinitionElement.getFilePermissions() != null) {
            fileDefinitionElement.getFilePermissions().getPropertyValueList().stream()
                    .filter(item -> IssFilePermissions.fromId(item.getPermission()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown permission"));
            fileDefinitionElement.getFilePermissions().getPropertyValueList().stream()
                    .filter(item -> IssCommonUserOrGroupIdentifier.fromId(item.getUserOrGroupIdentifier()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown user or group identifier"));
        }
    }

    private void checkForReferences(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (fileDefinitionElement.getFileTasks() != null) {
            fileDefinitionElement.getFileTasks().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (fileDefinitionElement.getFileComponents() != null) {
            fileDefinitionElement.getFileComponents().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    private void checkForRequiredProperties(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (fileDefinitionElement.getFileSource() == null) {
            annotationHolder.createErrorAnnotation(fileDefinitionElement, "Missing required section item 'Source'");
        }
        if (fileDefinitionElement.getFileDestDir() == null) {
            annotationHolder.createErrorAnnotation(fileDefinitionElement, "Missing required section item 'DestDir'");
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(fileDefinitionElement, annotationHolder);
        if (fileDefinitionElement.getParentSection() != null && fileDefinitionElement.getName() != null) {
            final long count = fileDefinitionElement.getParentSection().getDefinitionList().stream()
                    .filter(item -> item != fileDefinitionElement)
                    .filter(item -> item.getName() != null)
                    .filter(item -> item.getName().equalsIgnoreCase(fileDefinitionElement.getName()))
                    .count();
            if (count > 0) {
                annotationHolder.createWarningAnnotation(fileDefinitionElement, "File with source '" + fileDefinitionElement.getName() + "' already defined");
            }
        }
        checkForDoubleValues(fileDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (fileDefinitionElement.getFileFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    fileDefinitionElement.getFileFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
        if (fileDefinitionElement.getFileAttribute() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    fileDefinitionElement.getFileAttribute().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Attribute '" + key + "' already listed");
                    }
            );
        }
        if (fileDefinitionElement.getFilePermissions() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    fileDefinitionElement.getFilePermissions().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Permission '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (fileDefinitionElement.getFileTasks() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    fileDefinitionElement.getFileTasks().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (fileDefinitionElement.getFileComponents() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    fileDefinitionElement.getFileComponents().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
