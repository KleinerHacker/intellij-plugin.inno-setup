package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyINIFlagValueType implements PropertySpecialValueType {
    CreateKeyIfDoesNotExist("createKeyIfDoesntExist"),
    UninstallDeleteEntry("uninsDeleteEntry"),
    UninstallDeleteSection("uninsDeleteSection"),
    UninstallDeleteSectionIfEmpty("uninsDeleteSectionIfEmpty"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyINIFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyINIFlagValueType(@NotNull String name, boolean deprecated) {
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
