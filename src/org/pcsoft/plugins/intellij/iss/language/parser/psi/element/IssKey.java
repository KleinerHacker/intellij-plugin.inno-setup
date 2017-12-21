package org.pcsoft.plugins.intellij.iss.language.parser.psi.element;

import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

public interface IssKey extends PsiElement {
    String getName();
    IssSection getSection();
    PropertyType getPropertyType();
}
