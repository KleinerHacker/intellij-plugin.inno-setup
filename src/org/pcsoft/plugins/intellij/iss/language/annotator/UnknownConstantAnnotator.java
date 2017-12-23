package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssConstName;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssConstValue;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownConstantAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssConstValue) {
            final IssConstValue constValue = (IssConstValue) psiElement;
            final ConstantType constantType = constValue.getConstantType();

            if (constValue.getName() == null || constValue.getName().equals("%")) {
                annotationHolder.createErrorAnnotation(constValue, "No name for constant found");
            } else if (constantType == null) {
                final IssConstName constName = constValue.getConstName();
                if (constName != null) {
                    if (constName.getText().startsWith("%")) {
                        if (!System.getenv().containsKey(constName.getText().substring(1))) {
                            annotationHolder.createWarningAnnotation(constName, "Unknown environment variable, found were " +
                                    Arrays.toString(System.getenv().values().toArray()));
                        }
                    } else {
                        annotationHolder.createErrorAnnotation(constName, "Unknown constant, allowed are " +
                                Arrays.toString(Stream.of(ConstantType.getAllConstantTypes()).map(ConstantType::getName).toArray()))
                                .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                    }
                }
            }
        }
    }
}
