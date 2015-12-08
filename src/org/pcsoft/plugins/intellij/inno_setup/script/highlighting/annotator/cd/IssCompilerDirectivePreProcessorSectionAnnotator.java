package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectivePreProcessorSectionElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssCompilerDirectivePreProcessorSectionAnnotator extends IssAbstractCompilerDirectiveSectionAnnotator<IssCompilerDirectivePreProcessorSectionElement> {

    public IssCompilerDirectivePreProcessorSectionAnnotator() {
        super(IssCompilerDirectivePreProcessorSectionElement.class);
    }

    @Override
    protected void handleErrors(@NotNull IssCompilerDirectivePreProcessorSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleWarnings(@NotNull IssCompilerDirectivePreProcessorSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleInfo(@NotNull IssCompilerDirectivePreProcessorSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionElement.getPreProcessorTypeParameter() != null && sectionElement.getPreProcessorTypeParameter().getCompilerDirectivePreProcessorType() == null) {
            annotationHolder.createErrorAnnotation(sectionElement.getPreProcessorTypeParameter(), "Unknown preprocessor type");
        }
    }
}
