package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssCommonIOAttribute implements IssFlag {
    ReadOnly("ReadOnly", ""),
    Hidden("Hidden", ""),
    System("System", "")
    ;

    public static IssCommonIOAttribute fromId(final String id) {
        return IssFlag.findById(id, IssCommonIOAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssCommonIOAttribute(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssCommonIOAttribute(String id, String descriptionKey, boolean deprecated) {
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
