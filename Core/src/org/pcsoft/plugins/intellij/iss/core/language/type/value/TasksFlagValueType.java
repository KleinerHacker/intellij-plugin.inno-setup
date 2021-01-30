package org.pcsoft.plugins.intellij.iss.core.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsDeprecated;

public enum TasksFlagValueType implements SpecialValueType {
    CheckableAlone("checkableAlone"),
    CheckedOnce("checkedOnce"),
    DoNotInheritCheck("dontInheritCheck"),
    Exclusive("exclusive"),
    Restart("restart"),
    Unchecked("unchecked"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private TasksFlagValueType(@NotNull String name) {
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
