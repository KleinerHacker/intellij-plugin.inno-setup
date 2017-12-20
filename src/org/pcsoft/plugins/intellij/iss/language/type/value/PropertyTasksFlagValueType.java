package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyTasksFlagValueType implements PropertySpecialValueType {
    CheckableAlone("checkableAlone"),
    CheckedOnce("checkedOnce"),
    DoNotInheritCheck("dontInheritCheck"),
    Exclusive("exclusive"),
    Restart("restart"),
    Unchecked("unchecked"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyTasksFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyTasksFlagValueType(@NotNull String name, boolean deprecated) {
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
