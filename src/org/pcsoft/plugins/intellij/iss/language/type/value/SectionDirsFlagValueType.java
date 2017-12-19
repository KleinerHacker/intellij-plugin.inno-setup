package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

public enum SectionDirsFlagValueType implements SectionValueType {
    DeleteAfterInstall("deleteAfterInstall"),
    SetNTFSCompression("setNtfsCompression"),
    UninstallAlways("uninsAlwaysUninstall"),
    NeverAlways("uninsNeverUninstall"),
    UnsetNTFSCompression("unsetNtfsCompression"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private SectionDirsFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private SectionDirsFlagValueType(@NotNull String name, boolean deprecated) {
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
