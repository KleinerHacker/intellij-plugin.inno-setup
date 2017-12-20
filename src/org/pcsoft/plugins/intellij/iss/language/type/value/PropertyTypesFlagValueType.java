package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyTypesFlagValueType implements PropertySpecialValueType {
    IsCustom("isCustom"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyTypesFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyTypesFlagValueType(@NotNull String name, boolean deprecated) {
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
