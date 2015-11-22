package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssIconDefinitionElement;
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
        checkForKnownValues(annotationHolder, iconDefinitionElement::getIconFlags, p -> IssIconFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    private void checkForReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, iconDefinitionElement::getIconTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, iconDefinitionElement::getIconComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(iconDefinitionElement, annotationHolder);
        checkForDoubleValues(iconDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, iconDefinitionElement::getIconFlags, "Flag '%s' already listed");
    }
//
    private void checkForDoubleReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, iconDefinitionElement::getIconTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, iconDefinitionElement::getIconComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
