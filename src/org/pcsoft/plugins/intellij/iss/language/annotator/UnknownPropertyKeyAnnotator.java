package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssKey;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownPropertyKeyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssKey) {
            final IssKey key = (IssKey) psiElement;

            final IssSection section = key.getSection();
            if (section == null)
                return;
            final SectionType sectionType = section.getSectionType();
            if (sectionType == null || sectionType == SectionType.Messages || sectionType == SectionType.CustomMessages)
                return;
            final PropertyType[] propertyTypeEnumList = sectionType.getSectionPropertyClass().getEnumConstants();

            if (Stream.of(propertyTypeEnumList).noneMatch(sectionValue -> sectionValue.getName().equalsIgnoreCase(key.getName()))) {
                annotationHolder.createErrorAnnotation(psiElement, "Unable to find key for section " + section.getSectionTitle().getName() + ", allowed are " +
                        Arrays.toString(Stream.of(propertyTypeEnumList).map(PropertyType::getName).toArray()))
                        .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);

            }
        }
    }
}
