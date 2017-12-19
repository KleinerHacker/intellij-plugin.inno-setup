package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;

public interface SectionValueType {
    @NotNull
    String getName();
    boolean isDeprecated();
}
