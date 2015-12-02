package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssComponentFlag;

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
        checkForKnownValues(annotationHolder, componentDefinitionElement::getComponentFlags, p -> IssComponentFlag.fromId(p.getName()) == null, "Unknown flag");
        if (componentDefinitionElement.getComponentExtraDiskSpaceRequired() != null &&
                componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue() != null &&
                componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getNumber() != null) {
            if (componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getNumber() < 0) {
                annotationHolder.createErrorAnnotation(componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue(), "Extra Disk Space must be positive!");
            }
        }
    }

    private void checkForReferences(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, componentDefinitionElement::getComponentTypeReference, "Cannot find referenced type");
    }

    @Override
    protected void detectWarnings(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(componentDefinitionElement, annotationHolder);
        checkForDoubleReferences(componentDefinitionElement, annotationHolder);
        if (componentDefinitionElement.getComponentExtraDiskSpaceRequired() != null &&
                componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue() != null &&
                componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getNumber() != null) {
            if (componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue().getNumber() == 0) {
                annotationHolder.createWarningAnnotation(componentDefinitionElement.getComponentExtraDiskSpaceRequired().getPropertyValue(), "Extra Disk Space should be greater than 0!");
            }
        }
    }

    private void checkForDoubleValues(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, componentDefinitionElement::getComponentFlags, "Flag '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssComponentDefinitionElement componentDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, componentDefinitionElement::getComponentTypeReference, "Type '%s' already listed");
    }
}
