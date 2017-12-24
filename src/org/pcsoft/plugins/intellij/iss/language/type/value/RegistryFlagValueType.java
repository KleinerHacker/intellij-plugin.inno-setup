package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum RegistryFlagValueType implements SpecialValueType {
    CreateValueIfDoesNotExist("createValueIfDoesntExist"),
    DeleteKey("deleteKey"),
    DeleteValue("deleteValue"),
    DoNotCreateKey("dontCreateKey"),
    NoError("noError"),
    PreserveStringType("preserveStringType"),
    UninstallClearValue("uninsClearValue"),
    UninstallDeleteKey("uninsDeleteKey"),
    UninstallDeleteKeyIfEmpty("uninsDeleteKeyIfEmpty"),
    UninstallDeleteValue("uninsDeleteValue"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private RegistryFlagValueType(@NotNull String name) {
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
