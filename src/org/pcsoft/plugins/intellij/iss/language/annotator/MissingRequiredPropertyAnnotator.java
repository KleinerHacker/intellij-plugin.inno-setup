package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.util.List;
import java.util.stream.Stream;

public class MissingRequiredPropertyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFile) {
            final IssFile file = (IssFile) psiElement;
            for (SectionType sectionType : SectionType.values()) {
                if (!sectionType.isRequired())
                    continue;

                if (file.getSections() == null || !findRequiredSection(file.getSections(), sectionType)) {
                    final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(file, "Cannot find required section " + sectionType.getName());
                    errorAnnotation.setFileLevelAnnotation(true);
                }
            }
        } else if (psiElement instanceof IssSectionLine) {
            final IssSectionLine issSectionLine = (IssSectionLine) psiElement;
            if (!(issSectionLine instanceof IssMultipleSectionLine))
                return;
            final IssMultipleSectionLine multipleSectionLine = (IssMultipleSectionLine) issSectionLine;
            final IssSection issSection = issSectionLine.getSection();
            if (issSection == null)
                return;
            final SectionType sectionType = issSection.getSectionType();
            if (sectionType == null)
                return;
            final Class<? extends PropertyType> sectionValueClass = sectionType.getSectionPropertyClass();

            for (PropertyType propertyType : sectionValueClass.getEnumConstants()) {
                if (!propertyType.isRequired())
                    continue;

                if (multipleSectionLine.getMultiplePropertyList().isEmpty() || !findRequiredMultiProperty(multipleSectionLine, propertyType)) {
                    annotationHolder.createErrorAnnotation(issSectionLine, "Cannot find required property " + propertyType.getName())
                            .setAfterEndOfLine(true);
                }
            }
        } else if (psiElement instanceof IssSection) {
            final IssSection issSection = (IssSection) psiElement;
            final SectionType sectionType = issSection.getSectionType();
            if (sectionType == null)
                return;
            if (sectionType.getVariant() != SectionTypeVariant.Default)
                return;
            final List<IssDefaultSectionLine> sectionLineList = issSection.getDefaultSectionLineList();
            final Class<? extends PropertyType> sectionValueClass = sectionType.getSectionPropertyClass();

            for (PropertyType propertyType : sectionValueClass.getEnumConstants()) {
                if (!propertyType.isRequired())
                    continue;

                if (sectionLineList.isEmpty() || !findRequiredSingleProperty(sectionLineList, propertyType)) {
                    annotationHolder.createErrorAnnotation(issSection.getSectionTitle(), "Cannot find required property " + propertyType.getName());
                }
            }
        }
    }

    private boolean findRequiredSingleProperty(List<IssDefaultSectionLine> sectionLineList, PropertyType propertyType) {
        return sectionLineList.stream()
                .anyMatch(issSectionLine -> issSectionLine.getDefaultProperty().getName().equalsIgnoreCase(propertyType.getName()));
    }

    private boolean findRequiredMultiProperty(IssMultipleSectionLine multipleSectionLine, PropertyType propertyType) {
        return multipleSectionLine.getMultiplePropertyList().stream()
                .anyMatch(el -> el.getName().equalsIgnoreCase(propertyType.getName()));
    }

    private boolean findRequiredSection(IssSection[] sections, SectionType sectionType) {
        return Stream.of(sections)
                .anyMatch(issSection -> issSection.getSectionType() == sectionType);
    }
}
