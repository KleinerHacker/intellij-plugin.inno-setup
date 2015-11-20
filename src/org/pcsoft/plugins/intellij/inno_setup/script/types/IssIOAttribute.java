package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssIOAttribute implements IssPropertyValue {
    ReadOnly("ReadOnly", "common.attribute.readonly"),
    Hidden("Hidden", "common.attribute.hidden"),
    System("System", "common.attribute.system")
    ;

    public static IssIOAttribute fromId(final String id) {
        return IssPropertyValue.findById(id, IssIOAttribute.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssIOAttribute(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssIOAttribute(String id, String descriptionKey, boolean deprecated) {
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
