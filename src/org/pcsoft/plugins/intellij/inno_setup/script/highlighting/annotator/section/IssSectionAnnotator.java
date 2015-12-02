package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;

import java.util.stream.Stream;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFile) {
            findMissingSections((IssFile) psiElement, annotationHolder);
            findNotAllowedSections((IssFile) psiElement, annotationHolder);
            findDoubleSections((IssFile) psiElement, annotationHolder);
        }
    }

    private void findMissingSections(IssFile issFile, AnnotationHolder annotationHolder) {
        Stream.of(IssSectionType.values())
                .filter(item -> item.getFileType() == null || item.getFileType().getPsiFileClass() == issFile.getClass())
                .filter(IssSectionType::isRequired)
                .forEach(sectionType -> {
                    final IssSectionElement sectionElement = issFile.getSectionList().stream()
                            .filter(item -> item.getSectionName() != null)
                            .filter(item -> item.getSectionName().getName().equalsIgnoreCase(sectionType.getId()))
                            .findFirst().orElse(null);
                    if (sectionElement == null) {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(issFile, "Unable to find required section: " + sectionType.getId());
                        errorAnnotation.setFileLevelAnnotation(true);
                    }
                });
    }

    private void findNotAllowedSections(IssFile issFile, AnnotationHolder annotationHolder) {
        Stream.of(IssSectionType.values())
                .filter(item -> item.getFileType() != null && item.getFileType().getPsiFileClass() != issFile.getClass())
                .forEach(sectionType -> {
                    final IssSectionElement sectionElement = issFile.getSectionList().stream()
                            .filter(item -> item.getSectionName() != null)
                            .filter(item -> item.getSectionName().getName().equalsIgnoreCase(sectionType.getId()))
                            .findFirst().orElse(null);
                    if (sectionElement != null) {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(sectionElement, "Section '" + sectionType.getId() +
                                "' not allowed for file with extension '" + issFile.getVirtualFile().getExtension() + "'");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_SECTION_NOT_ALLOWED);
                    }
                });
    }

    private void findDoubleSections(IssFile issFile, AnnotationHolder annotationHolder) {
        IssAnnotatorUtils.findDoubleValues(
                issFile.getSectionList(),
                item -> item.getSectionName() != null,
                item -> item.getSectionName().getName(),
                (item, key) -> annotationHolder.createErrorAnnotation(item, "Section '" + key + "' already defined!")
        );
    }
}
