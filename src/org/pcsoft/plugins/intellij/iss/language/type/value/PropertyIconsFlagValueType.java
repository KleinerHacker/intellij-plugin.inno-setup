package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum PropertyIconsFlagValueType implements PropertySpecialValueType {
    CloseOnExit("closeOnExit"),
    CreateOnlyIfFileExists("createOnlyIfFileExists"),
    DoNotCloseOnExit("dontCloseOnExit"),
    ExcludeFromShowInNewWindow("excludeFromShowInNewWindow"),
    FolderShortcut("folderShortcut"),
    PreventPinning("preventPinning"),
    RunMaximized("runMaximized"),
    RunMinimized("runMinimized"),
    UninstallNever("uninsNeverUninstall"),
    UseAppPaths("useAppPaths"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyIconsFlagValueType(@NotNull String name) {
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
