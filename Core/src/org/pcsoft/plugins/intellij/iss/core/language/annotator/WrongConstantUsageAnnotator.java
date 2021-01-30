package org.pcsoft.plugins.intellij.iss.core.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssConstValue;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.ConstantType;

public class WrongConstantUsageAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssConstValue) {
            final IssConstValue constValue = (IssConstValue) psiElement;

            final ConstantType constantType = constValue.getConstantType();
            if (constantType == null)
                return;

            if (constantType.getArgumentCount() != 0) {
                if (constantType.getArgumentCount() < 0) {
                    if (constValue.getConstArgumentList().isEmpty()) {
                        annotationHolder.createErrorAnnotation(constValue, "Missing one argument as minimum");
                    }
                } else {
                    if (constValue.getConstArgumentList().size() != constantType.getArgumentCount()) {
                        annotationHolder.createErrorAnnotation(constValue, "Missing some arguments, found were " + constValue.getConstArgumentList().size() +
                                ", but expected are " + constantType.getArgumentCount());
                    }
                }
            }

            if (constantType.needDefault()) {
                if (constValue.getConstDefault() == null) {
                    annotationHolder.createErrorAnnotation(constValue, "Missing default value");
                }
            }
        }
    }
}
