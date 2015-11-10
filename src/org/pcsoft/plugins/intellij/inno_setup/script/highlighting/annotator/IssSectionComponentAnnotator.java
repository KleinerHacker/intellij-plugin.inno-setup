package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionComponentAnnotator extends IssAbstractSectionAnnotator<IssComponentDefinitionElement> {

    public IssSectionComponentAnnotator() {
        super(IssComponentDefinitionElement.class, DoubletCheckType.Error);
    }

    @Override
    protected void detectErrors(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(componentDefinitionElement, annotationHolder);
        checkForKnownValues(componentDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (componentDefinitionElement.getComponentFlags() != null) {
            componentDefinitionElement.getComponentFlags().getPropertyValueList().stream()
                    .filter(item -> IssComponentFlag.fromId(item.getName()) == null)
                    .forEach(item -> {
                        annotationHolder.createErrorAnnotation(item, "Unknown flag");
                    });
        }
        if (componentDefinitionElement.getComponentExtraDiskSpaceRequired() != null && componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue() != null) {
            if (componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getSize() < 0) {
                annotationHolder.createErrorAnnotation(componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue(), "Extra Disk Space must be positive!");
            }
        }
    }

    private void checkForReferences(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (componentDefinitionElement.getComponentTypes() != null) {
            componentDefinitionElement.getComponentTypes().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced type");
                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(componentDefinitionElement, annotationHolder);
        if (componentDefinitionElement.getComponentExtraDiskSpaceRequired() != null && componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue() != null) {
            if (componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getSize() == 0) {
                annotationHolder.createWarningAnnotation(componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue(), "Extra Disk Space should be greater than 0!");
            }
        }
    }

    private void checkForDoubleReferences(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (componentDefinitionElement.getComponentTypes() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    componentDefinitionElement.getComponentTypes().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Type '" + key + "' already listed");
                    }
            );
        }
        if (componentDefinitionElement.getComponentFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    componentDefinitionElement.getComponentFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }
}
