package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

public enum SectionFilesFlagValueType implements SectionValueType {
    Bit32("32bit"),
    Bit64("64bit"),
    AllowUnsafeFiles("allowUnsafeFiles"),
    CompareTimeStamp("compareTimeStamp"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private SectionFilesFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private SectionFilesFlagValueType(@NotNull String name, boolean deprecated) {
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
