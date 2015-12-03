package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallDeleteDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssDeleteType;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionInstallDeleteAnnotator extends IssAbstractSectionAnnotator<IssInstallDeleteDefinitionElement> {

    public IssSectionInstallDeleteAnnotator() {
        super(IssInstallDeleteDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(installDeleteDefinitionElement, annotationHolder);
        checkForKnownValues(installDeleteDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, installDeleteDefinitionElement::getInstallDeleteType, p -> IssDeleteType.fromId(p.getName()) == null, "Unknown delete type");
    }

    private void checkForReferences(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, installDeleteDefinitionElement::getInstallDeleteTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, installDeleteDefinitionElement::getInstallDeleteComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(installDeleteDefinitionElement, annotationHolder);
        checkForDoubleValues(installDeleteDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    private void checkForDoubleReferences(@NotNull IssInstallDeleteDefinitionElement installDeleteDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, installDeleteDefinitionElement::getInstallDeleteTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, installDeleteDefinitionElement::getInstallDeleteComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
