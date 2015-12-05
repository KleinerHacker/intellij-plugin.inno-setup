package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common.IssSectionNameElement;

/**
 * Created by Christoph on 15.12.2014.
 */
public class IssCommonAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssStringElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_STRING);
        } else if (psiElement instanceof IssSectionNameElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_SECTION_TITLE);
        }
    }




}
