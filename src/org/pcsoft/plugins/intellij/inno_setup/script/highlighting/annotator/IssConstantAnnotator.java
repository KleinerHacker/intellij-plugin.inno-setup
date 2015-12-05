package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssCompilerDirectiveConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssEnvironmentConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssMessageConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssAbstractReference;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public class IssConstantAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssConstantElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            if (psiElement instanceof IssCompilerDirectiveConstantElement) {
                handleCompilerDirectiveConstant((IssCompilerDirectiveConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssBuiltinConstantElement) {
                handleBuiltinConstant((IssBuiltinConstantElement) psiElement, infoAnnotation);
            } else if (psiElement instanceof IssMessageConstantElement) {
                handleMessageConstant(annotationHolder, infoAnnotation, (IssMessageConstantElement) psiElement);
            } else if (psiElement instanceof IssEnvironmentConstantElement) {
                handleEnvironmentConstant(annotationHolder, infoAnnotation, (IssEnvironmentConstantElement) psiElement);
            }
        }
    }

    private void handleMessageConstant(@NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation, IssMessageConstantElement messageConstantElement) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_MESSAGE);
        if (messageConstantElement.getConstantName() != null && messageConstantElement.getConstantName().getReference() != null &&
                ((IssAbstractReference)messageConstantElement.getConstantName().getReference()).multiResolve(false).length <= 0) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(messageConstantElement.getConstantName(), "No message reference found!");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
    }

    private void handleEnvironmentConstant(@NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation, IssEnvironmentConstantElement environmentConstantElement) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_ENVIRONMENT);
        if (environmentConstantElement.getConstantName() != null) {
            if (System.getenv(environmentConstantElement.getConstantName().getName()) == null) {
                final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(environmentConstantElement.getConstantName(),
                        "No environment variable with this name found currently!");
                errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
            }
        }
    }

    private void handleBuiltinConstant(@NotNull IssBuiltinConstantElement psiElement, Annotation infoAnnotation) {
        final IssBuiltinConstant constant = psiElement.getConstant();
        if (constant == null) {
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_BUILTIN_SHELL);
        } else {
            infoAnnotation.setTextAttributes(constant.getType().getTextAttributesKey());
        }
    }

    private void handleCompilerDirectiveConstant(@NotNull IssCompilerDirectiveConstantElement psiElement, @NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_COMPILER_DIRECTIVE);

        final IssCompilerDirectiveConstantElement constantElement = psiElement;
        if (constantElement.getConstantName() != null && constantElement.getConstantName().getReference() != null &&
                constantElement.getConstantName().getReference().resolve() == null) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(constantElement.getConstantName(), "No symbol reference found!");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
    }
}
