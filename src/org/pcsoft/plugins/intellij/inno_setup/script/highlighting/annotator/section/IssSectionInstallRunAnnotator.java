package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssInstallRunFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionInstallRunAnnotator extends IssAbstractSectionAnnotator<IssInstallRunDefinitionElement> {

    public IssSectionInstallRunAnnotator() {
        super(IssInstallRunDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(runDefinitionElement, annotationHolder);
        checkForKnownValues(runDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, runDefinitionElement::getInstallRunFlags, p -> IssInstallRunFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    private void checkForReferences(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, runDefinitionElement::getInstallRunTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, runDefinitionElement::getInstallRunComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(runDefinitionElement, annotationHolder);
        checkForDoubleValues(runDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, runDefinitionElement::getInstallRunFlags, "Flag '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, runDefinitionElement::getInstallRunTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, runDefinitionElement::getInstallRunComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
