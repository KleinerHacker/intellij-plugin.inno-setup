package org.pcsoft.plugins.intellij.iss.language.parser.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public abstract class IssAbstractElementManipulator<T extends PsiElement> implements ElementManipulator<T> {

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull T element) {
        if (StringUtils.isEmpty(((PsiNamedElement)element).getName()))
            return TextRange.EMPTY_RANGE;

        return new TextRange(0,((PsiNamedElement)element).getName().length());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T handleContentChange(@NotNull T element, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        return (T) ((PsiNamedElement)element).setName(s);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T handleContentChange(@NotNull T element, String s) throws IncorrectOperationException {
        return (T) ((PsiNamedElement)element).setName(s);
    }
}
