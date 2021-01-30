package org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

public interface IssProperty extends PsiElement {
    String getName();
    @NotNull
    IssKey getKey();
    PropertyType getPropertyType();
    @Nullable
    IssValue getValue();
    IssSection getSection();
}
