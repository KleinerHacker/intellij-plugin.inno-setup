package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyLanguagesElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyLanguagesValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyWindowsVersionElement;

/**
 * Created by Christoph on 21.11.2015.
 */
public class IssSectionCommonAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssPropertyLanguagesElement) {
            final IssPropertyLanguagesElement languagesElement = (IssPropertyLanguagesElement) psiElement;
            for (final IssPropertyLanguagesValueElement valueElement : languagesElement.getPropertyValueList()) {
                if (valueElement.getLocale() == null || (valueElement.getLocale().getISO3Country().trim().isEmpty() &&
                        valueElement.getLocale().getISO3Language().trim().isEmpty())) {
                    annotationHolder.createErrorAnnotation(valueElement, "Unknown locale!");
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
