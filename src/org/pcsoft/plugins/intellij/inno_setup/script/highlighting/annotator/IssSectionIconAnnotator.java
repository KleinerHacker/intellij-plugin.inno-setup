package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon.IssIconDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionIconAnnotator extends IssAbstractSectionAnnotator<IssIconDefinitionElement> {

    public IssSectionIconAnnotator() {
        super(IssIconDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(iconDefinitionElement, annotationHolder);
        checkForKnownValues(iconDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iconDefinitionElement.getIconFlags() != null) {
            iconDefinitionElement.getIconFlags().getPropertyValueList().stream()
                    .filter(item -> IssIconFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
    }

    private void checkForReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iconDefinitionElement.getIconTasks() != null) {
            iconDefinitionElement.getIconTasks().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (iconDefinitionElement.getIconComponents() != null) {
            iconDefinitionElement.getIconComponents().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(iconDefinitionElement, annotationHolder);
        if (iconDefinitionElement.getParentSection() != null && iconDefinitionElement.getName() != null) {
            final long count = iconDefinitionElement.getParentSection().getDefinitionList().stream()
                    .filter(item -> item != iconDefinitionElement)
                    .filter(item -> item.getName() != null)
                    .filter(item -> item.getName().equalsIgnoreCase(iconDefinitionElement.getName()))
                    .count();
            if (count > 0) {
                annotationHolder.createWarningAnnotation(iconDefinitionElement, "Icon with name '" + iconDefinitionElement.getName() + "' already defined");
            }
        }
        checkForDoubleValues(iconDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iconDefinitionElement.getIconFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iconDefinitionElement.getIconFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }
//
    private void checkForDoubleReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iconDefinitionElement.getIconTasks() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iconDefinitionElement.getIconTasks().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (iconDefinitionElement.getIconComponents() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iconDefinitionElement.getIconComponents().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
