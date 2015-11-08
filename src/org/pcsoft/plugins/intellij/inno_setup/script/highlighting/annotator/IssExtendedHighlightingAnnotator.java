package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssRegexUtils;

/**
 * Created by Christoph on 15.12.2014.
 */
public class IssExtendedHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssIdentifierElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_INFO_SECTION_ITEM_NAME);
        } else if (psiElement instanceof IssDefinitionPropertyValueElement) {
            IssRegexUtils.findInternalConstants(psiElement.getText(), textRange -> {
                final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(new TextRange(
                        psiElement.getTextOffset() + textRange.getStartOffset(),
                        psiElement.getTextOffset() + textRange.getEndOffset()), "Internal Constant");
                infoAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT);
            });
        }
    }
}
