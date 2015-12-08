package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolDefineSectionElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssCompilerDirectiveSymbolDefineSectionAnnotator extends IssAbstractCompilerDirectiveSectionAnnotator<IssCompilerDirectiveSymbolDefineSectionElement> {

    public IssCompilerDirectiveSymbolDefineSectionAnnotator() {
        super(IssCompilerDirectiveSymbolDefineSectionElement.class);
    }

    @Override
    protected void handleErrors(@NotNull IssCompilerDirectiveSymbolDefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleWarnings(@NotNull IssCompilerDirectiveSymbolDefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleInfo(@NotNull IssCompilerDirectiveSymbolDefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionElement.getVisibilityParameter() != null) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(sectionElement.getVisibilityParameter(), null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_PARAMETER_SPECIAL);
        }
    }
}
