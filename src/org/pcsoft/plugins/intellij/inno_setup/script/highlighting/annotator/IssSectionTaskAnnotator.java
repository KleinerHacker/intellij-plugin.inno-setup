package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionTaskAnnotator extends IssAbstractSectionAnnotator<IssTaskDefinitionElement> {

    public IssSectionTaskAnnotator() {
        super(IssTaskDefinitionElement.class, DoubletCheckType.Error);
    }

    @Override
    protected void detectErrors(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForRequiredProperties(taskDefinitionElement, annotationHolder);
        checkForReferences(taskDefinitionElement, annotationHolder);
        checkForKnownValues(taskDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (taskDefinitionElement.getTaskFlags() != null) {
            taskDefinitionElement.getTaskFlags().getPropertyValueList().stream()
                    .filter(item -> IssTaskFlag.fromId(item.getName()) == null)
                    .forEach(item -> {
                        annotationHolder.createErrorAnnotation(item, "Unknown flag");
                    });
        }
    }

    private void checkForReferences(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (taskDefinitionElement.getTaskComponents() != null) {
            taskDefinitionElement.getTaskComponents().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    private void checkForRequiredProperties(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (taskDefinitionElement.getTaskName() == null) {
            annotationHolder.createErrorAnnotation(taskDefinitionElement, "Missing required section item 'Name'");
        }
        if (taskDefinitionElement.getTaskDescription() == null) {
            annotationHolder.createErrorAnnotation(taskDefinitionElement, "Missing required section item 'Description'");
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(taskDefinitionElement, annotationHolder);
    }

    private void checkForDoubleReferences(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (taskDefinitionElement.getTaskComponents() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    taskDefinitionElement.getTaskComponents().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
        if (taskDefinitionElement.getTaskFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    taskDefinitionElement.getTaskFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }
}
