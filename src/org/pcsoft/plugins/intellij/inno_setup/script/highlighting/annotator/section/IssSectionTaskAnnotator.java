package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssTaskFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionTaskAnnotator extends IssAbstractSectionAnnotator<IssTaskDefinitionElement> {

    public IssSectionTaskAnnotator() {
        super(IssTaskDefinitionElement.class, DoubletCheckType.Error);
    }

    @Override
    protected void detectErrors(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(taskDefinitionElement, annotationHolder);
        checkForKnownValues(taskDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, taskDefinitionElement::getTaskFlags, p -> IssTaskFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    private void checkForReferences(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, taskDefinitionElement::getTaskComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(taskDefinitionElement, annotationHolder);
        checkForDoubleReferences(taskDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, taskDefinitionElement::getTaskFlags, "Flag '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssTaskDefinitionElement taskDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, taskDefinitionElement::getTaskComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
