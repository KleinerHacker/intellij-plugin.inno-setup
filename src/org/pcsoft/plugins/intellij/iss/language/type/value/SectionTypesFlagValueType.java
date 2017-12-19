package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

public enum SectionTypesFlagValueType implements SectionValueType {
    IsCustom("isCustom"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private SectionTypesFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private SectionTypesFlagValueType(@NotNull String name, boolean deprecated) {
        this.name = name;
        this.deprecated = deprecated;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
