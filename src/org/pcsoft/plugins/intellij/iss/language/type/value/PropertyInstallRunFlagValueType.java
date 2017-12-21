package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum PropertyInstallRunFlagValueType implements PropertySpecialValueType {
    Bit32("32Bit"),
    Bit64("64Bit"),
    HideWizard("hideWizard"),
    NoWait("noWait"),
    PostInstall("postInstall"),
    RunAsCurrentUser("runAsCurrentUser"),
    RunAsOriginalUser("runAsOriginalUser"),
    RunHidden("runHidden"),
    RunMaximized("runMaximized"),
    RunMinimized("runMinimized"),
    ShellExec("shellExec"),
    SkipIfDoesNotExist("skipIfDoesntExist"),
    SkipIfNotSilent("skipIfNotSilent"),
    SkipIfSilent("skipIfSilent"),
    Unchecked("unchecked"),
    WaitUntilIdle("waitUntilIdle"),
    WaitUntilTerminated("waitUntilTerminated"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private PropertyInstallRunFlagValueType(@NotNull String name) {
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
