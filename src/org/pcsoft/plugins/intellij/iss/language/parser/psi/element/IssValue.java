package org.pcsoft.plugins.intellij.iss.language.parser.psi.element;

import com.intellij.psi.PsiElement;

public interface IssValue extends PsiElement {
    IssSection getSection();
    IssProperty getProperty();
}
