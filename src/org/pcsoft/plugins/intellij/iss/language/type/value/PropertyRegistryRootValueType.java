package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum PropertyRegistryRootValueType implements PropertySpecialValueType {
    HKCR("HKCR"),
    HKCU("HKCU"),
    HKLM("HKLM"),
    HKU("HKU"),
    HKCC("HKCC"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyRegistryRootValueType(@NotNull String name) {
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
