package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssUninstallRunFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionUninstallRunAnnotator extends IssAbstractSectionAnnotator<IssUninstallRunDefinitionElement> {

    public IssSectionUninstallRunAnnotator() {
        super(IssUninstallRunDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(runDefinitionElement, annotationHolder);
        checkForKnownValues(runDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, runDefinitionElement::getUninstallRunFlags, p -> IssUninstallRunFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    private void checkForReferences(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, runDefinitionElement::getUninstallRunTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, runDefinitionElement::getUninstallRunComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(runDefinitionElement, annotationHolder);
        checkForDoubleValues(runDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, runDefinitionElement::getUninstallRunFlags, "Flag '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, runDefinitionElement::getUninstallRunTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, runDefinitionElement::getUninstallRunComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
