package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssCompilerDirectiveConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantNameElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssConstantAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssConstantNameElement) {
            if (PsiTreeUtil.getParentOfType(psiElement, IssCompilerDirectiveConstantElement.class) != null) {
                //Reference test
                if (psiElement.getReference().resolve() == null) {
                    final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(psiElement, "No symbol found!");
                    errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                }
            }
        }
    }
}
