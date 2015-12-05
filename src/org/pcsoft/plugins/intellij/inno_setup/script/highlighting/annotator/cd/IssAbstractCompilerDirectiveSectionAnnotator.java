package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.cd;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.common.IssStringElement;

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
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATION_INFO_COMPILER_DIRECTIVE);

            final T sectionElement = (T) psiElement;
            checkParameterList(sectionElement, annotationHolder);

            handleErrors(sectionElement, annotationHolder);
            handleWarnings(sectionElement, annotationHolder);
        }
    }

    protected abstract void handleErrors(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder);

    protected abstract void handleWarnings(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder);

    private void checkParameterList(@NotNull T sectionElement, @NotNull AnnotationHolder annotationHolder) {
        if (sectionElement.getParameterList().size() < sectionElement.getSectionType().getParameterIdentifiers().length) {
            annotationHolder.createErrorAnnotation(sectionElement, "Not all required parameters was found!");
            return;
        }

        for (IssCompilerDirectiveParameterElement parameterElement : sectionElement.getParameterList()) {
            switch (parameterElement.getParameterType().getValueType()) {
                case Boolean:
                case HexBinary:
                case DirectMultiple:
                case DirectMultipleWithNumber:
                    throw new RuntimeException("Not allowed here");
                case Unknown:
                    //Do nothing
                    break;
                case DirectSingle:
                case DirectSingleWithNumber:
                    if (PsiTreeUtil.findChildOfType(parameterElement, IssStringElement.class) != null) {
                        annotationHolder.createErrorAnnotation(parameterElement, "String not allowed for parameter '" + parameterElement.getParameterType().getName() + "'");
                    }
                    break;
                case String:
                    if (PsiTreeUtil.findChildOfType(parameterElement, IssStringElement.class) == null) {
                        annotationHolder.createErrorAnnotation(parameterElement, "Parameter '" + parameterElement.getParameterType().getName() + "' must be a string");
                    }
                    break;
                case Double:
                    try {
                        Double.parseDouble(parameterElement.getText());
                    } catch (NumberFormatException e) {
                        annotationHolder.createErrorAnnotation(parameterElement, "Parameter '" + parameterElement.getParameterType().getName() + "' must be a double value");
                    }
                    break;
                case Integer:
                    try {
                        Integer.parseInt(parameterElement.getText());
                    } catch (NumberFormatException e) {
                        annotationHolder.createErrorAnnotation(parameterElement, "Parameter '" + parameterElement.getParameterType().getName() + "' must be an integer value");
                    }
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }
}
