package org.pcsoft.plugins.intellij.iss.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssRefValue;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssValue;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

public class IssPropertyValueDocumentationProvider extends DocumentationProviderEx {
    
    //<editor-fold desc="Documentation Generation">
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssValue) {
            return buildDocumentation((IssValue) element);
        } else if (element instanceof IssRefValue) {
            return buildDocumentation((IssValue) element.getParent());
        }

        return null;
    }

    @Nullable
    private String buildDocumentation(IssValue value) {
        if (value.getSection() == null)
            return null;
        final SectionType sectionType = value.getSection().getSectionType();
        if (sectionType == null || sectionType == SectionType.Messages || sectionType == SectionType.CustomMessages)
            return null;
        if (!IssDocumentationBundle.containsSectionPropertyBundle(sectionType.getName()))
            return null;

        final PropertyType propertyType = value.getProperty().getPropertyType();
        if (propertyType == null)
            return null;

        final String bundleStringKey = propertyType.getName().toLowerCase() + "." + value.getText().toLowerCase();
        if (!IssDocumentationBundle.getSectionPropertyBundle(sectionType.getName()).containsKey(bundleStringKey))
            return null;
        return IssDocumentationBundle.getSectionPropertyBundle(sectionType.getName()).getString(bundleStringKey);
    }
    //</editor-fold>

    //<editor-fold desc="Element Check">
    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null &&
                ((contextElement.getParent() instanceof IssValue && checkSection((IssValue) contextElement.getParent()) ||
                        (contextElement.getParent() instanceof IssRefValue && checkSection((IssRefValue) contextElement.getParent())))))
            return contextElement.getParent();

        return null;
    }

    private static boolean checkSection(@NotNull IssValue value) {
        return value.getSection() != null && value.getSection().getSectionType() != null && value.getSection().getSectionType() != SectionType.Messages &&
                value.getSection().getSectionType() != SectionType.CustomMessages;
    }

    private static boolean checkSection(@NotNull IssRefValue value) {
        return checkSection((IssValue) value.getParent());
    }
    //</editor-fold>
}
