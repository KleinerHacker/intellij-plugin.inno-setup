package org.pcsoft.plugins.intellij.iss.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;

public enum PropertyFilesFlagValueType implements PropertySpecialValueType {
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

    private PropertyFilesFlagValueType(@NotNull String name) {
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
