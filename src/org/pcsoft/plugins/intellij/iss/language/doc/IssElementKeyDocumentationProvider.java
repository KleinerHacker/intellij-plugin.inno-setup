package org.pcsoft.plugins.intellij.iss.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssKey;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;

public class IssElementKeyDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssKey) {
            IssKey key = (IssKey) element;
            if (key.getSection() == null)
                return null;
            final SectionType sectionType = key.getSection().getSectionType();
            if (sectionType == null || sectionType == SectionType.Messages || sectionType == SectionType.CustomMessages)
                return null;
            if (!IssDocumentationBundle.containsSectionBundle(sectionType.getName()))
                return null;

            if (!IssDocumentationBundle.getSectionBundle(sectionType.getName()).containsKey("property." + key.getName().toLowerCase()))
                return null;
            return IssDocumentationBundle.getSectionBundle(sectionType.getName()).getString("property." + key.getName().toLowerCase());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null && contextElement.getParent() instanceof IssKey && checkSection((IssKey) contextElement.getParent()))
            return contextElement.getParent();

        return null;
    }

    private static boolean checkSection(@NotNull IssKey key) {
        return key.getSection() != null && key.getSection().getSectionType() != null && key.getSection().getSectionType() != SectionType.Messages &&
                key.getSection().getSectionType() != SectionType.CustomMessages;
    }
}
