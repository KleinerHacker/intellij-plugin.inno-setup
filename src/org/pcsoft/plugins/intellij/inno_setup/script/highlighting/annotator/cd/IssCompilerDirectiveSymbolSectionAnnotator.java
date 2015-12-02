package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolSectionElement;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssCompilerDirectiveSymbolSectionAnnotator extends IssAbstractCompilerDirectiveSectionAnnotator<IssCompilerDirectiveSymbolSectionElement> {

    public IssCompilerDirectiveSymbolSectionAnnotator() {
        super(IssCompilerDirectiveSymbolSectionElement.class);
    }

    @Override
    protected void handleErrors(@NotNull IssCompilerDirectiveSymbolSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void handleWarnings(@NotNull IssCompilerDirectiveSymbolSectionElement sectionElement, @NotNull AnnotationHolder annotationHolder) {

    }
}
