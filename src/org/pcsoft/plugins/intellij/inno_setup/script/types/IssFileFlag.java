package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssFileFlag implements IssFlag {
    $32Bit("32bit", "flag.files.32bit"),
    $64Bit("64bit", "flag.files.64bit"),
    AllowUnsafeFiles("AllowUnsafeFiles", "flag.files.allow_unsafe_files"),
    CompareTimeStamp("CompareTimeStamp", "flag.files.compare_time_stamp"),
    ConfirmOverwrite("ConfirmOverwrite", "flag.files.confirm_overwrite"),
    CreateAllSubDirs("CreateAllSubDirs", "flag.files.create_all_subdirs"),
    DeleteAfterInstall("DeleteAfterInstall", "flag.files.delete_after_install"),
    DoNotCopy("DontCopy", "flag.files.dont_copy"),
    DoNotVerifyChecksum("DontVerifyChecksum", "flag.files.dont_verify_checksum"),
    External("External", "flag.files.external"),
    FontIsNTTrueType("FontIsNTTrueType", "flag.files.font_id_nt_true_type"),
    GACInstall("GACInstall", "flag.files.gac_install"),
    IgnoreVersion("IgnoreVersion", "flag.files.ignore_version"),
    IsReadme("IsReadme", "flag.files.is_readme"),
    NoCompression("NoCompression", "flag.files.no_compression"),
    NoEncryption("NoEncryption", "flag.files.no_encryption"),
    NoRegError("NoRegError", "flag.files.no_reg_error"),
    OnlyIfDestinationFileExists("OnlyIfDestFileExists", "flag.files.only_if_dest_file_exists"),
    OnlyIfDoesNotExists("OnlyIfDoesntExists", "flag.files.only_if_doesnt_exists"),
    OverwriteReadOnly("OverwriteReadOnly", "flag.files.overwrite_readonly"),
    PromptIfOlder("PromptIfOlder", "flag.files.prompt_if_older"),
    RecurseSubDirs("RecurseSubDirs", "flag.files.recurse_subdirs"),
    RegServer("RegServer", "flag.files.reg_server"),
    RegTypeLib("RegTypeLib", "flag.files.reg_type_lib"),
    ReplaceSameVersion("ReplaceSameVersion", "flag.files.replace_same_version"),
    RestartReplace("RestartReplace", "flag.files.restart_replace"),
    SetNTFSCompression("SetNTFSCompression", "flag.files.set_ntfs_compression"),
    SharedFile("SharedFile", "flag.files.shared_file"),
    SkipIfSourceDoesNotExists("SkipIfSourceDoesntExists", "flag.files.skip_if_source_doesnt_exists"),
    SolidBreak("SolidBreak", "flag.files.solid_break"),
    SortFilesByExtension("SortFilesByExtension", "flag.files.sort_files_by_extension"),
    SortFilesByName("SortFilesByName", "flag.files.sort_files_by_name"),
    Touch("Touch", "flag.files.touch"),
    UninstallNoSharedFilePrompt("UninsNoSharedFilePrompt", "flag.files.unins_no_shared_file_propmt"),
    UninstallRemoveReadOnly("UninsRemoveReadOnly", "flag.files.unins_remove_readonly"),
    UninstallRestartDelete("UninsRestartDelete", "flag.files.unins_restart_delete"),
    UninstallNeverUninstall("UninsNeverUninstall", "flag.files.unins_nerver_uninstall"),
    UnsetNTFSCompression("UnsetNTFSCompression", "flag.files.unset_ntfs_compression");

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
