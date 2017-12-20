package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleSectionLine;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionLine;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;

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
            final IssMultipleSectionLine multiProperty = issSectionLine.getMultipleSectionLine();
            if (multiProperty == null)
                return;
            final IssSection issSection = issSectionLine.getSection();
            if (issSection == null)
                return;
            final SectionType sectionType = issSection.getSectionType();
            if (sectionType == null)
                return;
            final Class<? extends SectionProperty> sectionValueClass = sectionType.getSectionValueClass();

            for (SectionProperty sectionProperty : sectionValueClass.getEnumConstants()) {
                if (!sectionProperty.isRequired())
                    continue;

                if (multiProperty.getMultiplePropertyList().isEmpty() || !findRequiredMultiProperty(multiProperty, sectionProperty)) {
                    annotationHolder.createErrorAnnotation(issSectionLine, "Cannot find required property " + sectionProperty.getName())
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
            final List<IssSectionLine> sectionLineList = issSection.getSectionContent().getSectionLineList();
            final Class<? extends SectionProperty> sectionValueClass = sectionType.getSectionValueClass();

            for (SectionProperty sectionProperty : sectionValueClass.getEnumConstants()) {
                if (!sectionProperty.isRequired())
                    continue;

                if (sectionLineList.isEmpty() || !findRequiredSingleProperty(sectionLineList, sectionProperty)) {
                    annotationHolder.createErrorAnnotation(issSection.getSectionTitle(), "Cannot find required property " + sectionProperty.getName());
                }
            }
        }
    }

    private boolean findRequiredSingleProperty(List<IssSectionLine> sectionLineList, SectionProperty sectionProperty) {
        return sectionLineList.stream()
                .filter(issSectionLine -> issSectionLine.getDefaultSectionLine() != null)
                .anyMatch(issSectionLine -> issSectionLine.getDefaultSectionLine().getDefaultProperty().getName().equalsIgnoreCase(sectionProperty.getName()));
    }

    private boolean findRequiredMultiProperty(IssMultipleSectionLine multipleSectionLine, SectionProperty sectionProperty) {
        return multipleSectionLine.getMultiplePropertyList().stream()
                .anyMatch(el -> el.getName().equalsIgnoreCase(sectionProperty.getName()));
    }

    private boolean findRequiredSection(IssSection[] sections, SectionType sectionType) {
        return Stream.of(sections)
                .anyMatch(issSection -> issSection.getSectionType() == sectionType);
    }
}
