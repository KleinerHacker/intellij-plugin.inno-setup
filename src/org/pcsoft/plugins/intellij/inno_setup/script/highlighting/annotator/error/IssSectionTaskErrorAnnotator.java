package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.error;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionTaskErrorAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssTaskDefinitionElement) {
            final IssTaskDefinitionElement taskDefinitionElement = (IssTaskDefinitionElement) psiElement;
            if (taskDefinitionElement.getTaskName() == null) {
                annotationHolder.createErrorAnnotation(taskDefinitionElement, "Missing required section item 'Name'");
            }
            if (taskDefinitionElement.getTaskDescription() == null) {
                annotationHolder.createErrorAnnotation(taskDefinitionElement, "Missing required section item 'Description'");
            }
            if (taskDefinitionElement.getTaskComponents() != null) {
                taskDefinitionElement.getTaskComponents().getComponentsValueList().stream()
                        .filter(item -> item.getReference().resolve() == null)
                        .forEach(item -> {
                            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                            errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                        });
            }
            if (taskDefinitionElement.getParentSection() != null && taskDefinitionElement.getName() != null) {
                final long count = taskDefinitionElement.getParentSection().getDefinitionList().stream()
                        .filter(item -> item != taskDefinitionElement)
                        .filter(item -> item.getName() != null)
                        .filter(item -> item.getName().equals(taskDefinitionElement.getName()))
                        .count();
                if (count > 0) {
                    annotationHolder.createErrorAnnotation(taskDefinitionElement, "Task with name '" + taskDefinitionElement.getName() + "' already defined");
                }
            }
            if (taskDefinitionElement.getTaskFlags() != null) {
                taskDefinitionElement.getTaskFlags().getFlagsValueList().stream()
                        .filter(item -> IssTaskFlag.fromId(item.getName()) == null)
                        .forEach(item -> {
                            annotationHolder.createErrorAnnotation(item, "Unknown flag");
                        });
            }
        }
    }
}
