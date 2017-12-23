package org.pcsoft.plugins.intellij.iss.language.type.constant;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum CommonConstantType implements ConstantType {
    Cmd("cmd"),
    ComputerName("computerName"),
    Drive("drive", 1),
    GroupName("groupName"),
    Hwnd("hwnd"),
    WizardHwnd("wizardHwnd"),
    Ini("ini", 3, true),
    Language("language"),
    Cm("cm", -1),
    Reg("reg", 2, true),
    Param("param", 1, true),
    SrcExe("srcExe"),
    UninstallExe("uninstallExe"),
    SysUserInfoName("sysUserInfoName"),
    SysUserInfoOrg("sysUserInfoOrg"),
    UserInfoName("userInfoName"),
    UserInfoOrg("userInfoOrg"),
    UserInfoSerial("userInfoSerial"),
    UserName("userName"),
    Log("log"),
    ;

    @Nullable
    public static CommonConstantType fromName(@Nullable final String name) {
        if (StringUtils.isEmpty(name))
            return null;

        for (final CommonConstantType constantType : values()) {
            if (constantType.getName().equalsIgnoreCase(name))
                return constantType;
        }

        return null;
    }

    @NotNull
    private final String name;
    private final int argumentCount;
    private final boolean needDefault;
    private final boolean deprecated;

    private CommonConstantType(@NotNull String name) {
        this(name, 0);
    }

    private CommonConstantType(@NotNull String name, int argumentCount) {
        this(name, argumentCount, false);
    }

    private CommonConstantType(@NotNull String name, boolean needDefault) {
        this(name, 0, needDefault);
    }

    private CommonConstantType(@NotNull String name, int argumentCount, boolean needDefault) {
        this.name = name;
        this.argumentCount = argumentCount;
        this.needDefault = needDefault;

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

    public boolean needDefault() {
        return needDefault;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
