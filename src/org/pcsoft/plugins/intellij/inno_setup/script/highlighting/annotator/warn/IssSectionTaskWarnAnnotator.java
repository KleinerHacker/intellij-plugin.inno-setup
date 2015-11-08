package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.warn;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssSectionTaskWarnAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssTaskDefinitionElement) {
            final IssTaskDefinitionElement taskDefinitionElement = (IssTaskDefinitionElement) psiElement;
            if (taskDefinitionElement.getTaskComponents() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        taskDefinitionElement.getTaskComponents().getPropertyValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                        }
                );
            }
            if (taskDefinitionElement.getTaskFlags() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        taskDefinitionElement.getTaskFlags().getPropertyValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                        }
                );
            }
        }
    }
}
