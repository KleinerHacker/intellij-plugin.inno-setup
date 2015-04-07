package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.warn;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssSectionTypeWarnAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssTypeDefinitionElement) {
            final IssTypeDefinitionElement typeDefinitionElement = (IssTypeDefinitionElement) psiElement;

            if (typeDefinitionElement.getTypeFlags() != null) {
                IssAnnotatorUtils.findDoubleValues(
                        typeDefinitionElement.getTypeFlags().getFlagsValueList(),
                        element -> element.getName(),
                        (element, key) -> {
                            annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                        }
                );
            }
        }
    }
}
