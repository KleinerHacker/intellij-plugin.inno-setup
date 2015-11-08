package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionDirectoryAnnotator extends IssAbstractSectionAnnotator<IssDirectoryDefinitionElement> {

    public IssSectionDirectoryAnnotator() {
        super(IssDirectoryDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForRequiredProperties(directoryDefinitionElement, annotationHolder);
        checkForReferences(directoryDefinitionElement, annotationHolder);
        checkForKnownValues(directoryDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (directoryDefinitionElement.getDirectoryFlags() != null) {
            directoryDefinitionElement.getDirectoryFlags().getPropertyValueList().stream()
                    .filter(item -> IssDirectoryFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
        if (directoryDefinitionElement.getDirectoryAttribute() != null) {
            directoryDefinitionElement.getDirectoryAttribute().getPropertyValueList().stream()
                    .filter(item -> IssCommonIOAttribute.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown attribute"));
        }
        if (directoryDefinitionElement.getDirectoryPermissions() != null) {
            directoryDefinitionElement.getDirectoryPermissions().getPropertyValueList().stream()
                    .filter(item -> IssCommonIOPermissions.fromId(item.getPermission()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown permission"));
            directoryDefinitionElement.getDirectoryPermissions().getPropertyValueList().stream()
                    .filter(item -> IssCommonUserOrGroupIdentifier.fromId(item.getUserOrGroupIdentifier()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown user or group identifier"));
        }
    }

    private void checkForReferences(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (directoryDefinitionElement.getDirectoryTasks() != null) {
            directoryDefinitionElement.getDirectoryTasks().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (directoryDefinitionElement.getDirectoryComponents() != null) {
            directoryDefinitionElement.getDirectoryComponents().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    private void checkForRequiredProperties(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (directoryDefinitionElement.getDirectoryName() == null) {
            annotationHolder.createErrorAnnotation(directoryDefinitionElement, "Missing required section item 'Source'");
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(directoryDefinitionElement, annotationHolder);
        if (directoryDefinitionElement.getParentSection() != null && directoryDefinitionElement.getName() != null) {
            final long count = directoryDefinitionElement.getParentSection().getDefinitionList().stream()
                    .filter(item -> item != directoryDefinitionElement)
                    .filter(item -> item.getName() != null)
                    .filter(item -> item.getName().equalsIgnoreCase(directoryDefinitionElement.getName()))
                    .count();
            if (count > 0) {
                annotationHolder.createWarningAnnotation(directoryDefinitionElement, "Directory with source '" + directoryDefinitionElement.getName() + "' already defined");
            }
        }
        checkForDoubleValues(directoryDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (directoryDefinitionElement.getDirectoryFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    directoryDefinitionElement.getDirectoryFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
        if (directoryDefinitionElement.getDirectoryAttribute() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    directoryDefinitionElement.getDirectoryAttribute().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Attribute '" + key + "' already listed");
                    }
            );
        }
        if (directoryDefinitionElement.getDirectoryPermissions() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    directoryDefinitionElement.getDirectoryPermissions().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Permission '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (directoryDefinitionElement.getDirectoryTasks() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    directoryDefinitionElement.getDirectoryTasks().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (directoryDefinitionElement.getDirectoryComponents() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    directoryDefinitionElement.getDirectoryComponents().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
