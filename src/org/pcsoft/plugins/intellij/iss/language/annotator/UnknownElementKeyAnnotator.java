package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssKey;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.type.Section;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;

import java.util.Arrays;
import java.util.stream.Stream;

public class UnknownElementKeyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssKey) {
            final String elementKeyName = ((IssKey) psiElement).getName();

            final IssSection section = ((IssKey) psiElement).getSection();
            if (section == null)
                return;
            final Section sectionType = section.getSectionType();
            if (sectionType == null)
                return;
            final SectionValue[] sectionValueEnumList = sectionType.getSectionValueClass().getEnumConstants();

            if (Stream.of(sectionValueEnumList).noneMatch(sectionValue -> sectionValue.getName().equalsIgnoreCase(elementKeyName))) {
                annotationHolder.createErrorAnnotation(psiElement, "Unable to find key for section " + section.getSectionTitle().getName() + ", allowed are " +
                        Arrays.toString(Stream.of(sectionValueEnumList).map(SectionValue::getName).toArray()));

            }
        }
    }
}
