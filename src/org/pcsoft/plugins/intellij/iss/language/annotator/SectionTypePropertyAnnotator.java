package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssDefaultSectionLine;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleSectionLine;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionLine;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;

public class SectionTypePropertyAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssSectionLine) {
            final IssSectionLine sectionLine = (IssSectionLine) psiElement;

            final IssSection section = sectionLine.getSection();
            if (section == null)
                return;
            final SectionType sectionType = section.getSectionType();
            if (sectionType == null)
                return;

            switch (sectionType.getVariant()) {
                case Default:
                    if (!(sectionLine instanceof IssDefaultSectionLine)) {
                        annotationHolder.createErrorAnnotation(sectionLine, "You cannot use property lines in this section");
                    }
                    break;
                case LineBased:
                    if (!(sectionLine instanceof IssMultipleSectionLine)) {
                        annotationHolder.createErrorAnnotation(sectionLine, "You cannot use single properties in this section");
                    }
                    break;
                case Code:
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }
}
