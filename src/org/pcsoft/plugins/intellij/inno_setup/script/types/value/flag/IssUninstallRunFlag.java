package org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssUninstallRunFlag implements IssPropertyValue {
    $32Bit("32Bit", "uninstall_run.flag.32bit"),
    $64Bit("64Bit", "uninstall_run.flag.64bit"),
    HideWizard("HideWizard", "uninstall_run.flag.hidewizard"),
    NoWait("NoWait", "uninstall_run.flag.nowait"),
    RunAsCurrentUser("RunAsCurrentUser", "uninstall_run.flag.runascurrentuser"),
    RunHidden("RunHidden", "uninstall_run.flag.runhidden"),
    RunMaximized("RunMaximized", "uninstall_run.flag.runmaximized"),
    RunMinimized("RunMinimized", "uninstall_run.flag.runminimized"),
    ShellExec("ShellExec", "uninstall_run.flag.shellexec"),
    SkipIfDoesntExist("SkipIfDoesntExist", "uninstall_run.flag.skipifdoesntexist"),
    WaitUntilIdle("WaitUntilIdle", "uninstall_run.flag.waituntilidle"),
    WaitUntilTerminated("WaitUntilTerminated", "uninstall_run.flag.waituntilterminated"),
    ;

    public static IssUninstallRunFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssUninstallRunFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssUninstallRunFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssUninstallRunFlag(String id, String descriptionKey, boolean deprecated) {
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
