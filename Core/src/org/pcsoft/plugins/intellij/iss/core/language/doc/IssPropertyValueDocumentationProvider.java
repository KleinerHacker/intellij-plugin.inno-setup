package org.pcsoft.plugins.intellij.iss.core.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssRefValue;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssStringValue;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssValue;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

public class IssPropertyValueDocumentationProvider extends DocumentationProviderEx {
    
    //<editor-fold desc="Documentation Generation">
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssValue) {
            return buildDocumentation((IssValue) element);
        } else if (element instanceof IssRefValue && element.getParent() instanceof IssValue) {
            return buildDocumentation((IssValue) element.getParent());
        } else if (element instanceof IssRefValue && element.getParent() instanceof IssStringValue && element.getParent().getParent() instanceof IssValue) {
            return buildDocumentation((IssValue) element.getParent().getParent());
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
        if (!IssDocumentationBundle.containsPropertyBundle(sectionType.getName()))
            return null;

        final PropertyType propertyType = value.getProperty().getPropertyType();
        if (propertyType == null)
            return null;

        final String bundleStringKey = propertyType.getName().toLowerCase() + "." + value.getText().toLowerCase();
        if (!IssDocumentationBundle.getPropertyBundle(sectionType.getName()).containsKey(bundleStringKey))
            return null;
        return IssDocumentationBundle.getPropertyBundle(sectionType.getName()).getString(bundleStringKey);
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
        if (value.getParent() instanceof IssValue)
            return checkSection((IssValue) value.getParent());
        else if (value.getParent() instanceof IssStringValue && value.getParent().getParent() instanceof IssValue)
            return checkSection((IssValue) value.getParent().getParent());

        return false;
    }
    //</editor-fold>
}
