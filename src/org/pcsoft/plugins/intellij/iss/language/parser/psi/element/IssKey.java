package org.pcsoft.plugins.intellij.iss.language.parser.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

public interface IssKey extends PsiElement {
    @NotNull
    String getName();
    @Nullable
    IssSection getSection();
    @Nullable
    PropertyType getPropertyType();
}
