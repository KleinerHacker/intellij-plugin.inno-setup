package org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssInstallRunFlag implements IssPropertyValue {
    $32Bit("32Bit", "install_run.flag.32bit"),
    $64Bit("64Bit", "install_run.flag.64bit"),
    HideWizard("HideWizard", "install_run.flag.hidewizard"),
    NoWait("NoWait", "install_run.flag.nowait"),
    PostInstall("PostInstall", "install_run.flag.postinstall"),
    RunAsCurrentUser("RunAsCurrentUser", "install_run.flag.runascurrentuser"),
    RunAsOriginalUser("RunAsOriginalUser", "install_run.flag.runasoriginaluser"),
    RunHidden("RunHidden", "install_run.flag.runhidden"),
    RunMaximized("RunMaximized", "install_run.flag.runmaximized"),
    RunMinimized("RunMinimized", "install_run.flag.runminimized"),
    ShellExec("ShellExec", "install_run.flag.shellexec"),
    SkipIfDoesntExist("SkipIfDoesntExist", "install_run.flag.skipifdoesntexist"),
    SkipIfNotSilent("SkipIfNotSilent", "install_run.flag.skipifnotsilent"),
    SkipIfSilent("SkipIfSilent", "install_run.flag.skipifsilent"),
    Unchecked("Unchecked", "install_run.flag.unchecked"),
    WaitUntilIdle("WaitUntilIdle", "install_run.flag.waituntilidle"),
    WaitUntilTerminated("WaitUntilTerminated", "install_run.flag.waituntilterminated"),
    ;

    public static IssInstallRunFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssInstallRunFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssInstallRunFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssInstallRunFlag(String id, String descriptionKey, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.deprecated = deprecated;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
