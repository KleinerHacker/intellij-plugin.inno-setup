package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.highlighting.IssHighlighting;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;

public class PostHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssKey) {
            final TextAttributesKey attributesKey = IssHighlighting.KEYWORD;

            final Annotation annotation = annotationHolder.createInfoAnnotation(psiElement, null);
            annotation.setTextAttributes(attributesKey);
            if (((IssKey) psiElement).getPropertyType() != null && ((IssKey) psiElement).getPropertyType().isDeprecated()) {
                annotation.setEnforcedTextAttributes(new TextAttributes(attributesKey.getDefaultAttributes().getForegroundColor(),
                        attributesKey.getDefaultAttributes().getBackgroundColor(), attributesKey.getDefaultAttributes().getForegroundColor(),
                        EffectType.STRIKEOUT, attributesKey.getDefaultAttributes().getFontType()));
            }
        } else if (psiElement instanceof IssStringValue) {
            final Annotation annotation = annotationHolder.createInfoAnnotation(psiElement, null);
            annotation.setTextAttributes(IssHighlighting.STRING);
        } else if (psiElement instanceof IssConstValue) {
            final Annotation annotation = annotationHolder.createInfoAnnotation(psiElement, null);
            annotation.setTextAttributes(IssHighlighting.CONST);
        } else if (psiElement instanceof IssSectionTitle) {
            final TextAttributesKey attributesKey = IssHighlighting.LABEL;

            final Annotation annotation = annotationHolder.createInfoAnnotation(psiElement.getFirstChild().getNextSibling(), null);
            annotation.setTextAttributes(attributesKey);
            if (((IssSectionTitle) psiElement).getSectionType() != null && ((IssSectionTitle) psiElement).getSectionType().isDeprecated()) {
                annotation.setEnforcedTextAttributes(new TextAttributes(attributesKey.getDefaultAttributes().getForegroundColor(),
                        attributesKey.getDefaultAttributes().getBackgroundColor(), attributesKey.getDefaultAttributes().getForegroundColor(),
                        EffectType.STRIKEOUT, attributesKey.getDefaultAttributes().getFontType()));
            }
        } else if (psiElement instanceof IssPreprocessorDefine) {
            final IssPreprocessorDefine preprocessorDefine = (IssPreprocessorDefine) psiElement;
            annotationHolder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(IssHighlighting.PREPROCESSOR)
                    .range(preprocessorDefine.getPreprocessorName())
                    .create();
        } else if (psiElement instanceof IssPreprocessorType) {
            annotationHolder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(IssHighlighting.KEYWORD)
                    .range(psiElement.getNode())
                    .create();
        }
    }
}
