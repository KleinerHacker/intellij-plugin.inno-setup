package org.pcsoft.plugins.intellij.iss.language.parser.psi.element;

import com.intellij.psi.PsiElement;

public interface IssKey extends PsiElement {
    String getName();
    IssSection getSection();
}
