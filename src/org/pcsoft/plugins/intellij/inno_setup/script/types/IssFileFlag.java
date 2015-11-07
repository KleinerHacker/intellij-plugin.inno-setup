package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssFileFlag implements IssFlag {
    $32Bit("32bit", "file.flag.32bit"),
    $64Bit("64bit", "file.flag.64bit"),
    AllowUnsafeFiles("AllowUnsafeFiles", "file.flag.allow_unsafe_files"),
    CompareTimeStamp("CompareTimeStamp", "file.flag.compare_time_stamp"),
    ConfirmOverwrite("ConfirmOverwrite", "file.flag.confirm_overwrite"),
    CreateAllSubDirs("CreateAllSubDirs", "file.flag.create_all_subdirs"),
    DeleteAfterInstall("DeleteAfterInstall", "file.flag.delete_after_install"),
    DoNotCopy("DontCopy", "file.flag.dont_copy"),
    DoNotVerifyChecksum("DontVerifyChecksum", "file.flag.dont_verify_checksum"),
    External("External", "file.flag.external"),
    FontIsNTTrueType("FontIsNTTrueType", "file.flag.font_id_nt_true_type"),
    GACInstall("GACInstall", "file.flag.gac_install"),
    IgnoreVersion("IgnoreVersion", "file.flag.ignore_version"),
    IsReadme("IsReadme", "file.flag.is_readme"),
    NoCompression("NoCompression", "file.flag.no_compression"),
    NoEncryption("NoEncryption", "file.flag.no_encryption"),
    NoRegError("NoRegError", "file.flag.no_reg_error"),
    OnlyIfDestinationFileExists("OnlyIfDestFileExists", "file.flag.only_if_dest_file_exists"),
    OnlyIfDoesNotExists("OnlyIfDoesntExists", "file.flag.only_if_doesnt_exists"),
    OverwriteReadOnly("OverwriteReadOnly", "file.flag.overwrite_readonly"),
    PromptIfOlder("PromptIfOlder", "file.flag.prompt_if_older"),
    RecurseSubDirs("RecurseSubDirs", "file.flag.recurse_subdirs"),
    RegServer("RegServer", "file.flag.reg_server"),
    RegTypeLib("RegTypeLib", "file.flag.reg_type_lib"),
    ReplaceSameVersion("ReplaceSameVersion", "file.flag.replace_same_version"),
    RestartReplace("RestartReplace", "file.flag.restart_replace"),
    SetNTFSCompression("SetNTFSCompression", "file.flag.set_ntfs_compression"),
    SharedFile("SharedFile", "file.flag.shared_file"),
    SkipIfSourceDoesNotExists("SkipIfSourceDoesntExists", "file.flag.skip_if_source_doesnt_exists"),
    SolidBreak("SolidBreak", "file.flag.solid_break"),
    SortFilesByExtension("SortFilesByExtension", "file.flag.sort_files_by_extension"),
    SortFilesByName("SortFilesByName", "file.flag.sort_files_by_name"),
    Touch("Touch", "file.flag.touch"),
    UninstallNoSharedFilePrompt("UninsNoSharedFilePrompt", "file.flag.unins_no_shared_file_propmt"),
    UninstallRemoveReadOnly("UninsRemoveReadOnly", "file.flag.unins_remove_readonly"),
    UninstallRestartDelete("UninsRestartDelete", "file.flag.unins_restart_delete"),
    UninstallNeverUninstall("UninsNeverUninstall", "file.flag.unins_nerver_uninstall"),
    UnsetNTFSCompression("UnsetNTFSCompression", "file.flag.unset_ntfs_compression");

    public static IssFileFlag fromId(final String id) {
        return IssFlag.findById(id, IssFileFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssFileFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssFileFlag(String id, String descriptionKey, boolean deprecated) {
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
