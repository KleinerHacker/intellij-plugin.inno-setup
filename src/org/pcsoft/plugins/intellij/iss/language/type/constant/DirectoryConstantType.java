package org.pcsoft.plugins.intellij.iss.language.type.constant;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.*;

public enum DirectoryConstantType implements ConstantType {
    App("app"),
    Win("win"),
    Sys("sys"),
    SysWOW64("sysWOW64"),
    Src("src"),
    Sd("sd"),
    Pf("pf"),
    Pf32("pf32"),
    Pf64("pf64"),
    Cf("cf"),
    Cf32("cf32"),
    Cf64("cf64"),
    Tmp("tmp"),
    Fonts("fonts"),
    Dao("dao"),
    DotNet11("dotNet11"),
    DotNet20("dotNet20"),
    DotNet2032("dotNet2032"),
    DotNet2064("dotNet2064"),
    DotNet40("dotNet40"),
    DotNet4032("dotNet4032"),
    DotNet4064("dotNet4064"),
    ;

    @Nullable
    public static DirectoryConstantType fromName(@Nullable final String name) {
        if (StringUtils.isEmpty(name))
            return null;

        for (final DirectoryConstantType constantType : values()) {
            if (constantType.getName().equalsIgnoreCase(name))
                return constantType;
        }

        return null;
    }

    @NotNull
    private final String name;
    private final int argumentCount;
    private final boolean allowDefault;
    private final boolean deprecated;

    private DirectoryConstantType(@NotNull String name) {
        this(name, 0);
    }

    private DirectoryConstantType(@NotNull String name, int argumentCount) {
        this(name, argumentCount, false);
    }

    private DirectoryConstantType(@NotNull String name, boolean allowDefault) {
        this(name, 0, allowDefault);
    }

    private DirectoryConstantType(@NotNull String name, int argumentCount, boolean allowDefault) {
        this.name = name;
        this.argumentCount = argumentCount;
        this.allowDefault = allowDefault;

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
    public int getArgumentCount() {
        return argumentCount;
    }

    public boolean allowDefault() {
        return allowDefault;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
