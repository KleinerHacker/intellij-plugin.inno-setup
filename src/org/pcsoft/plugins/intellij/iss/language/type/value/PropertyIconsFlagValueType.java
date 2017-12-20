package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;

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
        this(name, false);
    }

    private PropertyIconsFlagValueType(@NotNull String name, boolean deprecated) {
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
