package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssFilePermissions implements IssFlag {
    Full("Full", "file.permissions.full"),
    Modify("Modify", "file.permissions.modify"),
    ReadExec("ReadExec", "file.permissions.readexec")
    ;

    public static IssFilePermissions fromId(final String id) {
        return IssFlag.findById(id, IssFilePermissions.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssFilePermissions(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssFilePermissions(String id, String descriptionKey, boolean deprecated) {
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
