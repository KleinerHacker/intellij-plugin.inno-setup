package org.pcsoft.plugins.intellij.iss.core.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssSectionTitle;

public class IssSectionDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssSectionTitle) {
            IssSectionTitle sectionTitle = (IssSectionTitle) element;
            if (sectionTitle.getName() == null)
                return null;

            if (!IssDocumentationBundle.getSectionBundle().containsKey(sectionTitle.getName().toLowerCase()))
                return null;
            return IssDocumentationBundle.getSectionBundle().getString(sectionTitle.getName().toLowerCase());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        IssSectionTitle issSectionTitle = PsiTreeUtil.getParentOfType(contextElement, IssSectionTitle.class);
        if (contextElement != null && issSectionTitle != null)
            return issSectionTitle;

        return null;
    }
}
