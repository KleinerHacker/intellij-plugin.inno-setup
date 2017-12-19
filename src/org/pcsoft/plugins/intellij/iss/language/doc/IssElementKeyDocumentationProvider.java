package org.pcsoft.plugins.intellij.iss.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssKey;

public class IssElementKeyDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssKey) {
            IssKey key = (IssKey) element;
            if (key.getSection() == null)
                return null;
            String sectionName = key.getSection().getName();
            if (!IssDocumentationBundle.containsSectionBundle(sectionName))
                return null;

            if (!IssDocumentationBundle.getSectionBundle(sectionName).containsKey("property." + key.getName().toLowerCase()))
                return null;
            return IssDocumentationBundle.getSectionBundle(sectionName).getString("property." + key.getName().toLowerCase());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null && contextElement.getParent() instanceof IssKey)
            return contextElement.getParent();

        return null;
    }
}
