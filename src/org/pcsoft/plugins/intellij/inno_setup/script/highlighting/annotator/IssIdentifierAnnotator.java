package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public class IssIdentifierAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) psiElement;
            handleIdentifierName(annotationHolder, identifierNameElement);
        } else if (psiElement instanceof IssIdentifierReferenceElement) {
            if (psiElement.getReference().resolve() == null) {
                final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(psiElement, "No language reference found!");
                errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
            } else {
                final Annotation hintAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
                hintAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_REF_LAN);
            }
        }
    }

    private void handleIdentifierName(@NotNull AnnotationHolder annotationHolder, IssIdentifierNameElement identifierNameElement) {
        final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(identifierNameElement, null);
        if (PsiTreeUtil.getParentOfType(identifierNameElement, IssSetupSectionElement.class) != null) {
            final IssSetupProperty setupProperty = IssSetupProperty.fromId(identifierNameElement.getName());
            if (setupProperty != null) {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.getAnnotationInfoPropertyName(
                        setupProperty.isRequired(), setupProperty.getClassifier()
                ));
            } else {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD);
            }
        } else if (PsiTreeUtil.getParentOfType(identifierNameElement, IssMessageSectionElement.class) != null ||
                PsiTreeUtil.getParentOfType(identifierNameElement, IssCustomMessageSectionElement.class) != null) {
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_LAN);
        } else {
            if (identifierNameElement.getParentProperty() != null) {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.getAnnotationInfoPropertyName(
                        identifierNameElement.getParentProperty().getPropertyType().isRequired(),
                        identifierNameElement.getParentProperty().getPropertyType().isDeprecated()
                ));
            } else {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD);
            }
        }
    }
}
