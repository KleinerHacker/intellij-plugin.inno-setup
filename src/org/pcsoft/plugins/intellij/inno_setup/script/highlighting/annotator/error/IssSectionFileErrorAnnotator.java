package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.error;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionFileErrorAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFileDefinitionElement) {
            final IssFileDefinitionElement fileDefinitionElement = (IssFileDefinitionElement) psiElement;
            if (fileDefinitionElement.getFileSource() == null) {
                annotationHolder.createErrorAnnotation(fileDefinitionElement, "Missing required section item 'Source'");
            }
            if (fileDefinitionElement.getFileDestDir() == null) {
                annotationHolder.createErrorAnnotation(fileDefinitionElement, "Missing required section item 'DestDir'");
            }
            if (fileDefinitionElement.getFileTasks() != null) {
                fileDefinitionElement.getFileTasks().getPropertyValueList().stream()
                        .filter(item -> item.getReference().resolve() == null)
                        .forEach(item -> {
                            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                            errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                        });
            }
            if (fileDefinitionElement.getFileComponents() != null) {
                fileDefinitionElement.getFileComponents().getPropertyValueList().stream()
                        .filter(item -> item.getReference().resolve() == null)
                        .forEach(item -> {
                            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                            errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                        });
            }
            if (fileDefinitionElement.getFileFlags() != null) {
                fileDefinitionElement.getFileFlags().getPropertyValueList().stream()
                        .filter(item -> IssFileFlag.fromId(item.getName()) == null)
                        .forEach(item -> {
                            annotationHolder.createErrorAnnotation(item, "Unknown flag");
                        });
            }
        }
    }
}
