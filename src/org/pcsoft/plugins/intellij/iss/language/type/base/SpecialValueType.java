package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;

public interface SpecialValueType extends TypeBase {
    @NotNull
    String getName();
    boolean isDeprecated();
}
