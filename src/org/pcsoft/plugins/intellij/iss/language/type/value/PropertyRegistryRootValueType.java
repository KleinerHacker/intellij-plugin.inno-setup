package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

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
        this(name, false);
    }

    private PropertyRegistryRootValueType(@NotNull String name, boolean deprecated) {
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
