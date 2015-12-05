package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;

/**
 * Created by pfeifchr on 02.12.2015.
 */
//TODO
public class IssCompilerDirectiveCommonAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssFile) {
            final Collection<IssCompilerDirectiveSymbolSectionElement> sectionElements = IssFindUtils.findCompilerDirectiveSymbolSectionElements(psiElement.getProject());
            IssAnnotatorUtils.findDoubleValues(sectionElements,
                    item -> item.getSymbolName() != null,
                    item -> item.getSymbolName().getName(),
                    ((element, key) -> annotationHolder.createErrorAnnotation(element, "Symbol with name '" + key + "' already defined!")));
        } else if (psiElement instanceof IssCompilerDirectiveParameterElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_COMPILER_DIRECTIVE_PARAMETER);
        }
    }
}
