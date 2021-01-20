package org.pcsoft.plugins.intellij.iss.language.type.constant;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum ShellFolderConstantType implements ConstantType {
    Group("group"),
    LocalAppData("localAppData"),
    SendTo("sendTo"),
    UserAppData("userAppData"),
    CommonAppData("commonAppData"),
    UserCf("userCf"),
    UserDesktop("userDesktop"),
    CommonDesktop("commonDesktop"),
    UserDocs("userDocs"),
    CommonDocs("commonDocs"),
    UserFavorites("userFavorites"),
    CommonFavorites("commonFavorites"),
    UserPf("userPf"),
    UserPrograms("userPrograms"),
    CommonPrograms("commonPrograms"),
    UserStartMenu("userStartMenu"),
    CommonStartMenu("commonStartMenu"),
    UserStartup("userStartup"),
    CommonStartup("commonStartup"),
    UserTemplates("userTemplates"),
    CommonTemplates("commonTemplates"),
    ;

    @Nullable
    public static ShellFolderConstantType fromName(@Nullable final String name) {
        if (StringUtils.isEmpty(name))
            return null;

        for (final ShellFolderConstantType constantType : values()) {
            if (constantType.getName().equalsIgnoreCase(name))
                return constantType;
        }

        return null;
    }

    private final String name;
    private final int argumentCount;
    private final boolean needDefault;
    private final boolean deprecated;

    private ShellFolderConstantType(String name) {
        this(name, 0);
    }

    private ShellFolderConstantType(String name, int argumentCount) {
        this(name, argumentCount, false);
    }

    private ShellFolderConstantType(String name, boolean needDefault) {
        this(name, 0, needDefault);
    }

    private ShellFolderConstantType(String name, int argumentCount, boolean needDefault) {
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
