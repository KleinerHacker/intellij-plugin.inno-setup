package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownSectionAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssSectionTitle) {
            final IssSectionTitle sectionTitle = (IssSectionTitle) psiElement;
            final SectionType sectionType = sectionTitle.getSectionType();

            if (sectionTitle.getName() == null) {
                annotationHolder.createErrorAnnotation(sectionTitle, "No name for section found");
            } else if (sectionType == null) {
                final ASTNode child = sectionTitle.getNode().findChildByType(IssGenTypes.NAME);
                if (child != null) {
                    annotationHolder.createErrorAnnotation(child, "Unknown section, allowed are " +
                            Arrays.toString(Stream.of(SectionType.values()).map(SectionType::getName).toArray()))
                            .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            }
        }
    }
}
