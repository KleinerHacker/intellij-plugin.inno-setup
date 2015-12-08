package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveIncludeSectionElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssCompilerDirectiveIncludeSectionAnnotator extends IssAbstractCompilerDirectiveSectionAnnotator<IssCompilerDirectiveIncludeSectionElement> {

    public IssCompilerDirectiveIncludeSectionAnnotator() {
        super(IssCompilerDirectiveIncludeSectionElement.class);
    }

    @Override
    protected void handleErrors(@NotNull IssCompilerDirectiveIncludeSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleWarnings(@NotNull IssCompilerDirectiveIncludeSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleInfo(@NotNull IssCompilerDirectiveIncludeSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }
}
