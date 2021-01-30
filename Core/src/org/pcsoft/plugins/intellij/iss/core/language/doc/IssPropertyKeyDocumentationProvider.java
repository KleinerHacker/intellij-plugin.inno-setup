package org.pcsoft.plugins.intellij.iss.core.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssKey;
import org.pcsoft.plugins.intellij.iss.core.language.type.SectionType;

public class IssPropertyKeyDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssKey) {
            IssKey key = (IssKey) element;

            if (key.getSection() == null)
                return null;
            final SectionType sectionType = key.getSection().getSectionType();
            if (sectionType == null || sectionType == SectionType.Messages || sectionType == SectionType.CustomMessages)
                return null;
            if (!IssDocumentationBundle.containsPropertyBundle(sectionType.getName()))
                return null;

            final String bundleStringKey = "property." + key.getName().toLowerCase();
            if (!IssDocumentationBundle.getPropertyBundle(sectionType.getName()).containsKey(bundleStringKey)) {
                if (!IssDocumentationBundle.getPropertyCommonBundle().containsKey(bundleStringKey))
                    return null;
                return IssDocumentationBundle.getPropertyCommonBundle().getString(bundleStringKey);
            }
            return IssDocumentationBundle.getPropertyBundle(sectionType.getName()).getString(bundleStringKey);
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        IssKey issKey = PsiTreeUtil.getParentOfType(contextElement, IssKey.class);
        if (contextElement != null && issKey != null && checkSection(issKey))
            return contextElement.getParent();

        return null;
    }

    private static boolean checkSection(@NotNull IssKey key) {
        return key.getSection() != null && key.getSection().getSectionType() != null && key.getSection().getSectionType() != SectionType.Messages &&
                key.getSection().getSectionType() != SectionType.CustomMessages;
    }
}
