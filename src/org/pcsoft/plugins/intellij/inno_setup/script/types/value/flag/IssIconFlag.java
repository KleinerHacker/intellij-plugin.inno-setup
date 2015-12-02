package org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssIconFlag implements IssPropertyValue {
    CloseOnExit("CloseOnExit", "icon.flag.closeonexit"),
    CreateOnlyIfFileExists("CreateOnlyIfFileExists", "icon.flag.createonlyiffileexists"),
    DontCloseOnExit("DontCloseOnExit", "icon.flag.dontcloseonexit"),
    ExcludeFromShowInNewInstall("ExcludeFromShowInNewInstall", "icon.flag.excludefromshowinnewinstall"),
    FolderShortCut("FolderShortCut", "icon.flag.foldershortcut"),
    PreventPinning("PreventPinning", "icon.flag.preventpinning"),
    RunMaximized("RunMaximized", "icon.flag.runmaximized"),
    RunMinimized("RunMinimized", "icon.flag.runminimized"),
    UninsNeverUninstall("UninsNeverUninstall", "icon.flag.uninsneveruninstall"),
    UseAppPaths("UseAppPaths", "icon.flag.useapppaths"),
    ;

    public static IssIconFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssIconFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssIconFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssIconFlag(String id, String descriptionKey, boolean deprecated) {
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
