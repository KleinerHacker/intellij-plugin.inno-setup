package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssPreprocessorName;
import org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownPreprocessorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssPreprocessorName) {
            final IssPreprocessorName preprocessorName = (IssPreprocessorName) psiElement;
            final PreprocessorType preprocessorType = preprocessorName.getPreprocessorType();

            if (preprocessorName.getName() == null || preprocessorName.getName().equals("#")) {
                annotationHolder.createErrorAnnotation(preprocessorName, "No name for preprocessor found");
            } else if (preprocessorType == null) {
                final ASTNode child = preprocessorName.getNode().findChildByType(IssGenTypes.NAME);
                if (child != null) {
                    annotationHolder.createErrorAnnotation(child, "Unknown preprocessor, allowed are " +
                            Arrays.toString(Stream.of(PreprocessorType.values()).map(PreprocessorType::getName).toArray()))
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            }
        }
    }
}
