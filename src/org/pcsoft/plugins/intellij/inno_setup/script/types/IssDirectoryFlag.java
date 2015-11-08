package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssDirectoryFlag implements IssFlag {
    DeleteAfterInstall("DeleteAfterInstall", "directory.flag.delete_after_install"),
    SetNTFSCompression("SetNTFSCompression", "directory.flag.set_ntfs_compression"),
    UninstallAlwaysUninstall("UninsAlwaysUninstall", "directory.flag.unins_always_uninstall"),
    UninstallNeverUninstall("UninsNeverUninstall", "directory.flag.unins_never_uninstall"),
    UnsetNTFSCompression("UnsetNTFSCompression", "directory.flag.unset_ntfs_compression");

    public static IssDirectoryFlag fromId(final String id) {
        return IssFlag.findById(id, IssDirectoryFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssDirectoryFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssDirectoryFlag(String id, String descriptionKey, boolean deprecated) {
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
