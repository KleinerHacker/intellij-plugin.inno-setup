package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum PropertyCompressionLevelValueType implements PropertySpecialValueType {
    Fast("fast"),
    Normal("normal"),
    Max("max"),
    Ultra("ultra"),
    Ultra64("ultra64"),
    None("none"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyCompressionLevelValueType(@NotNull String name) {
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
