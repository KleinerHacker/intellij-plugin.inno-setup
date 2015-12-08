package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolUndefineSectionElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssCompilerDirectiveSymbolUndefineSectionAnnotator extends IssAbstractCompilerDirectiveSectionAnnotator<IssCompilerDirectiveSymbolUndefineSectionElement> {

    public IssCompilerDirectiveSymbolUndefineSectionAnnotator() {
        super(IssCompilerDirectiveSymbolUndefineSectionElement.class);
    }

    @Override
    protected void handleErrors(@NotNull IssCompilerDirectiveSymbolUndefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionElement.getIdentifierParameter() != null && sectionElement.getIdentifierParameter().getReference() != null &&
                sectionElement.getIdentifierParameter().getReference().resolve() == null) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(sectionElement.getIdentifierParameter(), "No symbol reference found");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
    }

    @Override
    protected void handleWarnings(@NotNull IssCompilerDirectiveSymbolUndefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleInfo(@NotNull IssCompilerDirectiveSymbolUndefineSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionElement.getVisibilityParameter() != null) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(sectionElement.getVisibilityParameter(), null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_COMPILER_DIRECTIVE_PARAMETER_SPECIAL);
        }
    }
}
