package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssIOPermissions implements IssPropertyValue {
    Full("Full", "common.permissions.full"),
    Modify("Modify", "common.permissions.modify"),
    ReadExec("ReadExec", "common.permissions.readexec")
    ;

    public static IssIOPermissions fromId(final String id) {
        return IssPropertyValue.findById(id, IssIOPermissions.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssIOPermissions(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssIOPermissions(String id, String descriptionKey, boolean deprecated) {
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
