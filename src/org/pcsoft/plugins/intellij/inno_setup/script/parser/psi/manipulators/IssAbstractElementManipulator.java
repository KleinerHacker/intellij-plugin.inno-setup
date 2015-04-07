package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.manipulators;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;

public abstract class IssAbstractElementManipulator<T extends PsiElement> implements ElementManipulator<T> {

    @Override
    public TextRange getRangeInElement(T element) {
        return new TextRange(0,((PsiNamedElement)element).getName().length());
    }

    @Override
    public T handleContentChange(T element, TextRange textRange, String s) throws IncorrectOperationException {
        return (T) ((PsiNamedElement)element).setName(s);
    }

    @Override
    public T handleContentChange(T element, String s) throws IncorrectOperationException {
        return (T) ((PsiNamedElement)element).setName(s);
    }
}