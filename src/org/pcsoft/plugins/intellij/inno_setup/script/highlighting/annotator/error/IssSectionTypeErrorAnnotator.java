package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.error;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionTypeErrorAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssTypeDefinitionElement) {
            final IssTypeDefinitionElement typeDefinitionElement = (IssTypeDefinitionElement) psiElement;
            if (typeDefinitionElement.getTypeName() == null) {
                annotationHolder.createErrorAnnotation(typeDefinitionElement, "Missing required section item 'Name'");
            }
            if (typeDefinitionElement.getTypeDescription() == null) {
                annotationHolder.createErrorAnnotation(typeDefinitionElement, "Missing required section item 'Description'");
            }
            if (typeDefinitionElement.getParentSection() != null && typeDefinitionElement.getName() != null) {
                final long count = typeDefinitionElement.getParentSection().getDefinitionList().stream()
                        .filter(item -> item != typeDefinitionElement)
                        .filter(item -> item.getName() != null)
                        .filter(item -> item.getName().equals(typeDefinitionElement.getName()))
                        .count();
                if (count > 0) {
                    annotationHolder.createErrorAnnotation(typeDefinitionElement, "Task with name '" + typeDefinitionElement.getName() + "' already defined");
                }
            }
            if (typeDefinitionElement.getTypeFlags() != null) {
                typeDefinitionElement.getTypeFlags().getPropertyValueList().stream()
                        .filter(item -> IssTypeFlag.fromId(item.getName()) == null)
                        .forEach(item -> {
                            annotationHolder.createErrorAnnotation(item, "Unknown flag");
                        });
            }
        }
    }
}
