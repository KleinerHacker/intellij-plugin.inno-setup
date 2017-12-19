package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValueType;

public enum SectionFilesFlagValueType implements SectionValueType {
    Bit32("32bit"),
    Bit64("64bit"),
    AllowUnsafeFiles("allowUnsafeFiles"),
    CompareTimeStamp("compareTimeStamp"),
    ConfirmOverwrite("confirmOverwrite"),
    CreateAllSubDirs("createAllSubDirs"),
    DeleteAfterInstall("deleteAfterInstall"),
    DoNotCopy("dontCopy"),
    DoNotVerifyChecksum("dontVerifyChecksum"),
    External("external"),
    FontIsNotTrueType("FontIsntTrueType"),
    GACInstall("gacInstall"),
    IgnoreVersion("ignoreVersion"),
    IsReadMe("isReadMe"),
    NoCompression("noCompression"),
    NoEncryption("noEncryption"),
    NoRegError("noRegError"),
    OnlyIfDestFileExists("onlyIfDestFileExists"),
    OnlyIfDoesNotExist("onlyIfDoesntExist"),
    OverwriteReadOnly("overwriteReadOnly"),
    PromptIfOlder("promptIfOlder"),
    RecurseSubDirectories("recurseSubDirs"),
    RegServer("regServer"),
    RegTypeLib("regTypeLib"),
    ReplaceSameVersion("replaceSameVersion"),
    RestartReplace("restartReplace"),
    SetNTFSCompression("setNtfsCompression"),
    SharedFile("sharedFile"),
    Sign("sign"),
    SignOnce("signOnce"),
    SkipIfSourceDoesNotExist("skipIfSourceDoesntExist"),
    SolidBreak("solidBreak"),
    SortFilesByExtension("sortFilesByExtension"),
    SortFilesByName("sortFilesByName"),
    Touch("touch"),
    UninstallNoSharedFilePrompt("uninsNoSharedFilePrompt"),
    UninstallRemoveReadOnly("uninsRemoveReadOnly"),
    UninstallRestartDelete("uninsRestartDelete"),
    UninstallNever("uninsNeverUninstall"),
    UnsetNTFSCompression("unsetNtfsCompression"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private SectionFilesFlagValueType(@NotNull String name) {
        this(name, false);
    }

    private SectionFilesFlagValueType(@NotNull String name, boolean deprecated) {
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
