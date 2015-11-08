package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.error;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionComponentErrorAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssComponentDefinitionElement) {
            final IssComponentDefinitionElement componentDefinitionElement = (IssComponentDefinitionElement) psiElement;
            if (componentDefinitionElement.getComponentName() == null) {
                annotationHolder.createErrorAnnotation(componentDefinitionElement, "Missing required section item 'Name'");
            }
            if (componentDefinitionElement.getComponentDescription() == null) {
                annotationHolder.createErrorAnnotation(componentDefinitionElement, "Missing required section item 'Description'");
            }
            if (componentDefinitionElement.getParentSection() != null && componentDefinitionElement.getName() != null) {
                final long count = componentDefinitionElement.getParentSection().getDefinitionList().stream()
                        .filter(item -> item != componentDefinitionElement)
                        .filter(item -> item.getName() != null)
                        .filter(item -> item.getName().equals(componentDefinitionElement.getName()))
                        .count();
                if (count > 0) {
                    annotationHolder.createErrorAnnotation(componentDefinitionElement, "Component with name '" + componentDefinitionElement.getName() + "' already defined");
                }
            }
            if (componentDefinitionElement.getComponentTypes() != null) {
                componentDefinitionElement.getComponentTypes().getPropertyValueList().stream()
                        .filter(item -> item.getReference().resolve() == null)
                        .forEach(item -> {
                            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced type");
                            errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                        });
            }
            if (componentDefinitionElement.getComponentFlags() != null) {
                componentDefinitionElement.getComponentFlags().getPropertyValueList().stream()
                        .filter(item -> IssComponentFlag.fromId(item.getName()) == null)
                        .forEach(item -> {
                            annotationHolder.createErrorAnnotation(item, "Unknown flag");
                        });
            }
        }
    }
}
