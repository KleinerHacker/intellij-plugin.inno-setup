package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;

import java.util.stream.Stream;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public abstract class IssAbstractCompilerDirectiveSectionAnnotator<T extends IssCompilerDirectiveSectionElement> implements Annotator {
    private final Class<T> sectionClass;

    public IssAbstractCompilerDirectiveSectionAnnotator(Class<T> sectionClass) {
        this.sectionClass = sectionClass;
    }

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionClass.isAssignableFrom(psiElement.getClass())) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE);

            final T sectionElement = (T) psiElement;
            checkParameterList(sectionElement, annotationHolder);

            handleErrors(sectionElement, annotationHolder);
            handleWarnings(sectionElement, annotationHolder);
            handleInfo(sectionElement, annotationHolder);
        }
    }

    protected void handleInfo(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder) {
        //Do nothing
    }

    protected abstract void handleErrors(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder);

    protected abstract void handleWarnings(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder);

    private void checkParameterList(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder) {
        final long count = Stream.of(sectionElement.getSectionType().getParameterIdentifiers())
                .filter(item -> !item.isOptional())
                .count();
        if (sectionElement.getParameterList().size() < count) {
            annotationHolder.createErrorAnnotation(sectionElement, "Not all required parameters was found!");
            return;
        }
        //TODO
    }
}
