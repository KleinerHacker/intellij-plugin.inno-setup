package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

public enum SectionAttributesValueType implements SectionValueType {
    ReadOnly("readOnly"),
    Hidden("hidden"),
    System("system"),
    NotContentIndexed("notContentIndexed"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private SectionAttributesValueType(@NotNull String name) {
        this(name, false);
    }

    private SectionAttributesValueType(@NotNull String name, boolean deprecated) {
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
