package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssTaskFlag implements IssFlag {
    CheckableAlone("CheckableAlone", "flag.tasks.checkable_alone"),
    CheckedOnce("CheckedOnce", "flag.tasks.checked_once"),
    DoNotInheritCheck("DontInheritCheck", "flag.tasks.dont_inherit_check"),
    Exclusive("Exclusive", "flag.tasks.exclusive"),
    Restart("Restart", "flag.tasks.restart"),
    Unchecked("Unchecked", "flag.tasks.unchecked");

    public static IssTaskFlag fromId(final String id) {
        return IssFlag.findById(id, IssTaskFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssTaskFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssTaskFlag(String id, String descriptionKey, boolean deprecated) {
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
