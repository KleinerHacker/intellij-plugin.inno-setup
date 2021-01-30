package org.pcsoft.plugins.intellij.iss.core.language.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssConstValue;

public class IssConstantDocumentationProvider extends DocumentationProviderEx {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssConstValue) {
            IssConstValue constValue = (IssConstValue) element;
            if (constValue.getName() == null)
                return null;

            if (!IssDocumentationBundle.getConstantBundle().containsKey(constValue.getName().toLowerCase()))
                return null;
            return IssDocumentationBundle.getConstantBundle().getString(constValue.getName().toLowerCase());
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        IssConstValue issConstValue = PsiTreeUtil.getParentOfType(contextElement, IssConstValue.class);
        if (contextElement != null && issConstValue != null)
            return issConstValue;

        return null;
    }
}
