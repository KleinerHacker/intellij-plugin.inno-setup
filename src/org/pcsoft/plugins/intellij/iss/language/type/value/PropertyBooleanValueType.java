package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyBooleanValueType implements PropertySpecialValueType {
    True("yes"),
    False("no"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyBooleanValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyBooleanValueType(@NotNull String name, boolean deprecated) {
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
