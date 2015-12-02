package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common.IssSectionNameElement;

/**
 * Created by Christoph on 15.12.2014.
 */
public class IssExtendedHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssIdentifierNameElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME);
        } else if (psiElement instanceof IssIdentifierReferenceElement) {
            final Annotation hintAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            hintAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_LAN);
        } else if (psiElement instanceof IssStringElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_STRING);
        } else if (psiElement instanceof IssConstantElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            if (psiElement.getText().startsWith("{#")) {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_CONSTANT);
            } else {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT);
            }
        } else if (psiElement instanceof IssCompilerDirectiveSectionElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_COMPILER_DIRECTIVE);
        } else if (psiElement instanceof IssCompilerDirectiveParameterElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_COMPILER_DIRECTIVE_PARAMETER);
        } else if (psiElement instanceof IssSectionNameElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_SECTION_TITLE);
        }
    }
}
