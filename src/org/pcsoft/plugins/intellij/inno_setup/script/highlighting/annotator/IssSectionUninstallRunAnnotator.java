package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssUninstallRunFlag;

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
        if (runDefinitionElement.getUninstallRunFlags() != null) {
            runDefinitionElement.getUninstallRunFlags().getPropertyValueList().stream()
                    .filter(item -> IssUninstallRunFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
    }

    private void checkForReferences(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getUninstallRunTaskReference() != null) {
            runDefinitionElement.getUninstallRunTaskReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (runDefinitionElement.getUninstallRunComponentReference() != null) {
            runDefinitionElement.getUninstallRunComponentReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(runDefinitionElement, annotationHolder);
        checkForDoubleValues(runDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getUninstallRunFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getUninstallRunFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssUninstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getUninstallRunTaskReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getUninstallRunTaskReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (runDefinitionElement.getUninstallRunComponentReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getUninstallRunComponentReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
