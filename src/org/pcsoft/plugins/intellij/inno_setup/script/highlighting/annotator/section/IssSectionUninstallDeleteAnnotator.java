package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallDeleteDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssDeleteType;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionUninstallDeleteAnnotator extends IssAbstractSectionAnnotator<IssUninstallDeleteDefinitionElement> {

    public IssSectionUninstallDeleteAnnotator() {
        super(IssUninstallDeleteDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(uninstallDeleteDefinitionElement, annotationHolder);
        checkForKnownValues(uninstallDeleteDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, uninstallDeleteDefinitionElement::getUninstallDeleteType, p -> IssDeleteType.fromId(p.getName()) == null, "Unknown delete type");
    }

    private void checkForReferences(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, uninstallDeleteDefinitionElement::getUninstallDeleteTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, uninstallDeleteDefinitionElement::getUninstallDeleteComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(uninstallDeleteDefinitionElement, annotationHolder);
        checkForDoubleValues(uninstallDeleteDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    private void checkForDoubleReferences(@NotNull IssUninstallDeleteDefinitionElement uninstallDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, uninstallDeleteDefinitionElement::getUninstallDeleteTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, uninstallDeleteDefinitionElement::getUninstallDeleteComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
