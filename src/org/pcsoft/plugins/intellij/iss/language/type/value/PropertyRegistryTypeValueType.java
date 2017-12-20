package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

public enum PropertyRegistryTypeValueType implements PropertySpecialValueType {
    None("none"),
    String("string"),
    ExpandSZ("expandSZ"),
    MultiSZ("multiSZ"),
    DWord("dWord"),
    QWord("qWord"),
    Binary("binary"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyRegistryTypeValueType(@NotNull String name) {
        this(name, false);
    }

    private PropertyRegistryTypeValueType(@NotNull String name, boolean deprecated) {
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
