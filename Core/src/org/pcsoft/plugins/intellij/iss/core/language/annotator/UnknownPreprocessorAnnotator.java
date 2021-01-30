package org.pcsoft.plugins.intellij.iss.core.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssPreprocessorElement;
import org.pcsoft.plugins.intellij.iss.core.language.type.PreprocessorType;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownPreprocessorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssPreprocessorElement) {
            final IssPreprocessorElement preprocessorElement = (IssPreprocessorElement) psiElement;
            final PreprocessorType preprocessorType = preprocessorElement.getType();

            if (preprocessorType == PreprocessorType.Define) {
                if (preprocessorElement.getType() == null) {
                    annotationHolder
                            .newAnnotation(HighlightSeverity.ERROR, "No name for preprocessor found")
                            .range(preprocessorElement)
                            .create();
                }
            } else if (preprocessorType == null) {
                final ASTNode child = preprocessorElement.getNode().findChildByType(IssGenTypes.NAME);
                if (child != null) {
                    annotationHolder
                            .newAnnotation(HighlightSeverity.ERROR, "Unknown preprocessor, allowed are " +
                                    Arrays.toString(Stream.of(PreprocessorType.values()).map(PreprocessorType::getName).toArray()))
                            .range(child)
                            .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                            .create();
                }
            }
        }
    }
}
