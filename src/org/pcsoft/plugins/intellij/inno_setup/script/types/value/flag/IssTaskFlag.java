package org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssTaskFlag implements IssPropertyValue {
    CheckableAlone("CheckableAlone", "task.flag.checkable_alone"),
    CheckedOnce("CheckedOnce", "task.flag.checked_once"),
    DoNotInheritCheck("DontInheritCheck", "task.flag.dont_inherit_check"),
    Exclusive("Exclusive", "task.flag.exclusive"),
    Restart("Restart", "task.flag.restart"),
    Unchecked("Unchecked", "task.flag.unchecked");

    public static IssTaskFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssTaskFlag.class);
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
