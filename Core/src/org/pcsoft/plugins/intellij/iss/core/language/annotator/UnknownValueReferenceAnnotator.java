package org.pcsoft.plugins.intellij.iss.core.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssRefValue;
import org.pcsoft.plugins.intellij.iss.core.language.reference.IssComponentsReference;
import org.pcsoft.plugins.intellij.iss.core.language.reference.IssTasksReference;
import org.pcsoft.plugins.intellij.iss.core.language.reference.IssTypesReference;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

public class UnknownValueReferenceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssRefValue) {
            final IssProperty property = PsiTreeUtil.getParentOfType(psiElement, IssProperty.class);
            if (property == null)
                return;
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null)
                return;

            if (propertyType.getReferenceTargetSectionType() == SectionType.Types) {
                if (new IssTypesReference((IssRefValue) psiElement, true).multiResolve(true).length <= 0) {
                    annotationHolder.createErrorAnnotation(psiElement, "Unable to find type definition")
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            } else if (propertyType.getReferenceTargetSectionType() == SectionType.Tasks) {
                if (new IssTasksReference((IssRefValue) psiElement, true).multiResolve(true).length <= 0) {
                    annotationHolder.createErrorAnnotation(psiElement, "Unable to find task definition")
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            } else if (propertyType.getReferenceTargetSectionType() == SectionType.Components) {
                if (new IssComponentsReference((IssRefValue) psiElement, true).multiResolve(true).length <= 0) {
                    annotationHolder.createErrorAnnotation(psiElement, "Unable to find component definition")
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            }
        }
    }
}
