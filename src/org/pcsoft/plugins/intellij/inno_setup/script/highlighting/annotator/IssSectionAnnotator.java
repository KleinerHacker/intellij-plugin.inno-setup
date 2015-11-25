package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;

import java.util.stream.Stream;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFile) {
            findMissingSections((IssFile) psiElement, annotationHolder);
            findDoubleSections((IssFile) psiElement, annotationHolder);
        }
    }

    private void findMissingSections(IssFile issFile, AnnotationHolder annotationHolder) {
        Stream.of(IssSectionType.values())
                .filter(IssSectionType::isRequired)
                .forEach(sectionType -> {
                    final IssSectionElement sectionElement = issFile.getSectionList().stream()
                            .filter(item -> item.getSectionNameElement() != null)
                            .filter(item -> item.getSectionNameElement().getName().equalsIgnoreCase(sectionType.getId()))
                            .findFirst().orElse(null);
                    if (sectionElement == null) {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(issFile, "Unable to find required section: " + sectionType.getId());
                        errorAnnotation.setFileLevelAnnotation(true);
                    }
                });
    }

    private void findDoubleSections(IssFile issFile, AnnotationHolder annotationHolder) {
        IssAnnotatorUtils.findDoubleValues(
                issFile.getSectionList(),
                item -> item.getSectionNameElement() != null,
                item -> item.getSectionNameElement().getName(),
                (item, key) -> annotationHolder.createErrorAnnotation(item, "Section '" + key + "' already defined!")
        );
    }
}
