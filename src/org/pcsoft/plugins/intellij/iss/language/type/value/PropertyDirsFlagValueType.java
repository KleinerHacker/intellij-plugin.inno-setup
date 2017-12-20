package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyDirsFlagValueType implements PropertySpecialValueType {
    DeleteAfterInstall("deleteAfterInstall"),
    SetNTFSCompression("setNtfsCompression"),
    UninstallAlways("uninsAlwaysUninstall"),
    NeverAlways("uninsNeverUninstall"),
    UnsetNTFSCompression("unsetNtfsCompression"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyDirsFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyDirsFlagValueType(@NotNull String name, boolean deprecated) {
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
