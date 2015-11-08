package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssCommonIOPermissions implements IssFlag {
    Full("Full", "common.permissions.full"),
    Modify("Modify", "common.permissions.modify"),
    ReadExec("ReadExec", "common.permissions.readexec")
    ;

    public static IssCommonIOPermissions fromId(final String id) {
        return IssFlag.findById(id, IssCommonIOPermissions.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssCommonIOPermissions(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssCommonIOPermissions(String id, String descriptionKey, boolean deprecated) {
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
