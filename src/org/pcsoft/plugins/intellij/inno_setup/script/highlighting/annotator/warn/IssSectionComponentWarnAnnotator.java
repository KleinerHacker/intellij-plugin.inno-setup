package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.warn;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssSectionComponentWarnAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssComponentDefinitionElement) {
            final IssComponentDefinitionElement componentDefinitionElement = (IssComponentDefinitionElement) psiElement;
            if (componentDefinitionElement.getComponentTypes() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        componentDefinitionElement.getComponentTypes().getTypesValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Type '" + key + "' already listed");
                        }
                );
            }
            if (componentDefinitionElement.getComponentFlags() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        componentDefinitionElement.getComponentFlags().getFlagsValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                        }
                );
            }
        }
    }
}
