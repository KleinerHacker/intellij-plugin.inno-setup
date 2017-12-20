package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyAttributesValueType implements PropertySpecialValueType {
    ReadOnly("readOnly"),
    Hidden("hidden"),
    System("system"),
    NotContentIndexed("notContentIndexed"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyAttributesValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyAttributesValueType(@NotNull String name, boolean deprecated) {
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
