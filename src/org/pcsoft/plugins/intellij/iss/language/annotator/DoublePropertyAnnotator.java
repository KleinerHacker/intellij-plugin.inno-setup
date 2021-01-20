package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;

import java.util.List;
import java.util.stream.Collectors;

public class DoublePropertyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssSection) {
            final IssSection section = (IssSection) psiElement;
            //Initial checks
            final SectionType sectionType = section.getSectionType();
            if (sectionType == null)
                return;
            if (sectionType.getVariant() != SectionTypeVariant.Default)
                return;
            if (section.getDefaultSectionLineList().isEmpty())
                return;

            //Find all single properties in section
            final List<IssDefaultProperty> defaultProperties = section.getDefaultSectionLineList().stream()
                    .map(IssDefaultSectionLine::getDefaultProperty)
                    .collect(Collectors.toList());
            //Find all double single properties
            for (IssDefaultProperty defaultProperty : defaultProperties) {
                defaultProperties.stream()
                        .filter(item -> item != defaultProperty)
                        .filter(item -> item.getName().equalsIgnoreCase(defaultProperty.getName()))
                        .forEach(item -> annotationHolder.createErrorAnnotation(item.getDefaultKey(), "Property was already defined in this section"));
            }
        } else if (psiElement instanceof IssMultipleSectionLine) {
            final IssMultipleSectionLine multipleSectionLine = (IssMultipleSectionLine) psiElement;
            if (multipleSectionLine.getMultiplePropertyList().isEmpty())
                return;

            for (IssMultipleProperty multipleProperty : multipleSectionLine.getMultiplePropertyList()) {
                multipleSectionLine.getMultiplePropertyList().stream()
                        .filter(item -> item != multipleProperty)
                        .filter(item -> item.getName().equalsIgnoreCase(multipleProperty.getName()))
                        .forEach(item -> annotationHolder.createErrorAnnotation(item.getMultipleKey(), "Property was already defined in this section line"));
            }
        }
    }
}
