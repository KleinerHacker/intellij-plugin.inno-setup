package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssComponentFlag implements IssFlag {
    CheckableAlone("CheckableAlone", "component.flag.checkable_alone"),
    DoNotInheritCheck("DontInheritCheck", "component.flag.dont_inherit_check"),
    Exclusive("Exclusive", "component.flag.exclusive"),
    Fixed("Fixed", "component.flag.fixed"),
    Restart("Restart", "component.flag.restart"),
    DisableNoUninstallWarnings("DisableNoUninstallWarnings", "component.flag.disable_no_uninstall_warning");

    public static IssComponentFlag fromId(final String id) {
        return IssFlag.findById(id, IssComponentFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssComponentFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssComponentFlag(String id, String descriptionKey, boolean deprecated) {
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
