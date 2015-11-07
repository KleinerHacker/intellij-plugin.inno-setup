package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssTypeFlag implements IssFlag {
    IsCustom("IsCustom", "type.flag.is_custom");

    public static IssTypeFlag fromId(final String id) {
        return IssFlag.findById(id, IssTypeFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssTypeFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssTypeFlag(String id, String descriptionKey, boolean deprecated) {
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
