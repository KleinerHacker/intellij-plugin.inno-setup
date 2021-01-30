package org.pcsoft.plugins.intellij.iss.core.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsDeprecated;

public enum INIFlagValueType implements SpecialValueType {
    CreateKeyIfDoesNotExist("createKeyIfDoesntExist"),
    UninstallDeleteEntry("uninsDeleteEntry"),
    UninstallDeleteSection("uninsDeleteSection"),
    UninstallDeleteSectionIfEmpty("uninsDeleteSectionIfEmpty"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private INIFlagValueType(@NotNull String name) {
        this.name = name;

        try {
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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
