package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunFlag;

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
        if (runDefinitionElement.getInstallRunFlags() != null) {
            runDefinitionElement.getInstallRunFlags().getPropertyValueList().stream()
                    .filter(item -> IssInstallRunFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
    }

    private void checkForReferences(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getInstallRunTaskReference() != null) {
            runDefinitionElement.getInstallRunTaskReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (runDefinitionElement.getInstallRunComponentReference() != null) {
            runDefinitionElement.getInstallRunComponentReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(runDefinitionElement, annotationHolder);
        checkForDoubleValues(runDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getInstallRunFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getInstallRunFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssInstallRunDefinitionElement runDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (runDefinitionElement.getInstallRunTaskReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getInstallRunTaskReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (runDefinitionElement.getInstallRunComponentReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    runDefinitionElement.getInstallRunComponentReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
