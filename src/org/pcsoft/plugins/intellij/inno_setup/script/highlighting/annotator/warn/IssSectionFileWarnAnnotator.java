package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.warn;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionFileWarnAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFileDefinitionElement) {
            final IssFileDefinitionElement fileDefinitionElement = (IssFileDefinitionElement) psiElement;
            if (fileDefinitionElement.getFileTasks() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        fileDefinitionElement.getFileTasks().getPropertyValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                        }
                );
            }
            if (fileDefinitionElement.getFileComponents() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        fileDefinitionElement.getFileComponents().getPropertyValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                        }
                );
            }
            if (fileDefinitionElement.getParentSection() != null && fileDefinitionElement.getName() != null) {
                final long count = fileDefinitionElement.getParentSection().getDefinitionList().stream()
                        .filter(item -> item != fileDefinitionElement)
                        .filter(item -> item.getName() != null)
                        .filter(item -> item.getName().equals(fileDefinitionElement.getName()))
                        .count();
                if (count > 0) {
                    annotationHolder.createWarningAnnotation(fileDefinitionElement, "File with source '" + fileDefinitionElement.getName() + "' already defined");
                }
            }
            if (fileDefinitionElement.getFileFlags() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        fileDefinitionElement.getFileFlags().getPropertyValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                        }
                );
            }
        }
    }
}
