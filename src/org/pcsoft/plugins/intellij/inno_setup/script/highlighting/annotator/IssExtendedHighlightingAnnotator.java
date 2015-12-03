package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssCompilerDirectiveConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssMessageConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPresentationHint;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSetupPropertyClassifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

/**
 * Created by Christoph on 15.12.2014.
 */
public class IssExtendedHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) psiElement;
            handleIdentifierName(annotationHolder, identifierNameElement);
        } else if (psiElement instanceof IssIdentifierReferenceElement) {
            if (psiElement.getReference().resolve() == null) {
                final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(psiElement, "No language reference found!");
                errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
            } else {
                final Annotation hintAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
                hintAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_REF);
            }
        } else if (psiElement instanceof IssStringElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_STRING);
        } else if (psiElement instanceof IssConstantElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            if (psiElement instanceof IssCompilerDirectiveConstantElement) {
                handleCompilerDirectiveConstant((IssCompilerDirectiveConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssBuiltinConstantElement) {
                handleBuiltinConstant((IssBuiltinConstantElement) psiElement, infoAnnotation);
            } else if (psiElement instanceof IssMessageConstantElement) {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_MESSAGE);
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

    private void handleBuiltinConstant(@NotNull IssBuiltinConstantElement psiElement, Annotation infoAnnotation) {
        final IssBuiltinConstant constant = psiElement.getConstant();
        if (constant == null) {
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_DEFAULT);
        } else {
            infoAnnotation.setTextAttributes(TextAttributesKey.createTextAttributesKey(constant.getType().name(),
                    IssPresentationHint.buildFrom(constant.getType(), constant.isDeprecated())));
        }
    }

    private void handleCompilerDirectiveConstant(@NotNull IssCompilerDirectiveConstantElement psiElement, @NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_CONSTANT);

        final IssCompilerDirectiveConstantElement constantElement = psiElement;
        if (constantElement.getConstantName() != null && constantElement.getConstantName().getReference() != null &&
                constantElement.getConstantName().getReference().resolve() == null) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(constantElement.getConstantName(), "No symbol reference found!");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
    }

    private void handleIdentifierName(@NotNull AnnotationHolder annotationHolder, IssIdentifierNameElement identifierNameElement) {
        final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(identifierNameElement, null);
        if (PsiTreeUtil.getParentOfType(identifierNameElement, IssSetupSectionElement.class) != null) {
            final IssSetupProperty setupProperty = IssSetupProperty.fromId(identifierNameElement.getName());
            if (setupProperty != null) {
                infoAnnotation.setTextAttributes(TextAttributesKey.createTextAttributesKey(setupProperty.getClassifier().name(), new TextAttributes(
                        setupProperty.getClassifier().getTextColor(), null, setupProperty.getClassifier().getTextColor(),
                        setupProperty.getClassifier() == IssSetupPropertyClassifier.Obsolete ? EffectType.STRIKEOUT : null,
                        setupProperty.getClassifier().getFontStyle()
                )));
            } else {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD);
            }
        } else if (PsiTreeUtil.getParentOfType(identifierNameElement, IssMessageSectionElement.class) != null ||
                PsiTreeUtil.getParentOfType(identifierNameElement, IssCustomMessageSectionElement.class) != null) {
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_LAN);
        } else {
            if (identifierNameElement.getParentProperty() != null) {
                if (identifierNameElement.getParentProperty().getPropertyType().isRequired()) {
                    infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_REQUIRED);
                } else if (identifierNameElement.getParentProperty().getPropertyType().isDeprecated()) {
                    infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_DEPRECATED);
                } else {
                    infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD);
                }
            } else {
                infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_STANDARD);
            }
        }
    }
}
