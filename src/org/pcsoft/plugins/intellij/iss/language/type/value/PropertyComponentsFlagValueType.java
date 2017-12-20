package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyComponentsFlagValueType implements PropertySpecialValueType {
    CheckableAlone("checkableAlone"),
    DoNotInheritCheck("dontInheritCheck"),
    Exclusive("exclusive"),
    Fixed("fixed"),
    Restart("restart"),
    DisableNoUninstallWarning("disableNoUninstallWarning"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyComponentsFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyComponentsFlagValueType(@NotNull String name, boolean deprecated) {
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
