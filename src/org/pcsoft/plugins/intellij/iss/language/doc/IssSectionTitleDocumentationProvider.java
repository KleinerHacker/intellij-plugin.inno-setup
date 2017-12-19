package org.pcsoft.plugins.intellij.iss.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;

public class IssSectionTitleDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssSectionTitle) {
            IssSectionTitle sectionTitle = (IssSectionTitle) element;
            if (sectionTitle.getName() == null)
                return null;

            if (!IssDocumentationBundle.getSectionTitleBundle().containsKey(sectionTitle.getName().toLowerCase()))
                return null;
            return IssDocumentationBundle.getSectionTitleBundle().getString(sectionTitle.getName().toLowerCase());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null && contextElement.getParent() instanceof IssSectionTitle)
            return contextElement.getParent();

        return null;
    }
}
