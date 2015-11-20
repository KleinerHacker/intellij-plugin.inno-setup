package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssRunFlag implements IssPropertyValue {
    $32Bit("32Bit", "run.flag.32bit"),
    $64Bit("64Bit", "run.flag.64bit"),
    HideWizard("HideWizard", "run.flag.hidewizard"),
    NoWait("NoWait", "run.flag.nowait"),
    PostInstall("PostInstall", "run.flag.postinstall"),
    RunAsCurrentUser("RunAsCurrentUser", "run.flag.runascurrentuser"),
    RunAsOriginalUser("RunAsOriginalUser", "run.flag.runasoriginaluser"),
    RunHidden("RunHidden", "run.flag.runhidden"),
    RunMaximized("RunMaximized", "run.flag.runmaximized"),
    RunMinimized("RunMinimized", "run.flag.runminimized"),
    ShellExec("ShellExec", "run.flag.shellexec"),
    SkipIfDoesntExist("SkipIfDoesntExist", "run.flag.skipifdoesntexist"),
    SkipIfNotSilent("SkipIfNotSilent", "run.flag.skipifnotsilent"),
    SkipIfSilent("SkipIfSilent", "run.flag.skipifsilent"),
    Unchecked("Unchecked", "run.flag.unchecked"),
    WaitUntilIdle("WaitUntilIdle", "run.flag.waituntilidle"),
    WaitUntilTerminated("WaitUntilTerminated", "run.flag.waituntilterminated"),
    ;

    public static IssRunFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssRunFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssRunFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssRunFlag(String id, String descriptionKey, boolean deprecated) {
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
