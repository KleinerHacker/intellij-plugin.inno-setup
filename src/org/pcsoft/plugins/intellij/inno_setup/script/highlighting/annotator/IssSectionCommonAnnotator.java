package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyLanguagesReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyLanguagesReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyWindowsVersionElement;

/**
 * Created by Christoph on 21.11.2015.
 */
public class IssSectionCommonAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssPropertyLanguagesReferenceElement) {
            final IssPropertyLanguagesReferenceElement languagesElement = (IssPropertyLanguagesReferenceElement) psiElement;
            for (final IssPropertyLanguagesReferenceValueElement valueElement : languagesElement.getPropertyValueList()) {
                if (valueElement.getReference().resolve() == null) {
                    final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(valueElement, "Unknown locale reference!");
                    errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                }
            }

            IssAnnotatorUtils.findDoubleValues(
                    languagesElement.getPropertyValueList(),
                    PsiElement::getText,
                    (item, key) -> annotationHolder.createWarningAnnotation(item, "Locale already defined: " + key)
            );
        } else if (psiElement instanceof IssPropertyWindowsVersionElement) {
            final IssPropertyWindowsVersionElement windowsVersionElement = (IssPropertyWindowsVersionElement) psiElement;
            if (windowsVersionElement.getPropertyValue() != null) {
                if (!windowsVersionElement.getPropertyValue().getVersion().matches("([0-9]+\\.[0-9]+)(,?[0-9]+\\.[0-9]+)*")) {
                    annotationHolder.createErrorAnnotation(windowsVersionElement.getPropertyValue(),
                            "Windows Version in wrong format: Must be X.Y or X.Y,A.B, e. g. 4.1 or 3.1,5.0");
                }
            }
        }
    }
}
