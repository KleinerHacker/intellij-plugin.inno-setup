package org.pcsoft.plugins.intellij.iss.core.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsDeprecated;

public enum ColorValueType implements SpecialValueType {
    Black("clBlack"),
    Green("clGreen"),
    Maroon("clMaroon"),
    Olive("clOlive"),
    Navy("clNavy"),
    Purple("clPurple"),
    Teal("clTeal"),
    Gray("clGray"),
    Silver("clSilver"),
    Red("clRed"),
    Lime("clLime"),
    Yellow("clYellow"),
    Blue("clBlue"),
    Fuchsia("clFuchsia"),
    Aqua("clAqua"),
    White("clWhite"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private ColorValueType(@NotNull String name) {
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
