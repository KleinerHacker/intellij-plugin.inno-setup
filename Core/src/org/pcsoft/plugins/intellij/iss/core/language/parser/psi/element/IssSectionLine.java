package org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element;

import com.intellij.psi.PsiElement;

public interface IssSectionLine extends PsiElement {
    IssSection getSection();
}
