package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssRegistryValueType implements IssPropertyValue {
    None("None", "registry.valuetype.none"),
    String("String", "registry.valuetype.string"),
    ExpandSZ("ExpandSZ", "registry.valuetype.expandsz"),
    MultiSZ("MultiSZ", "registry.valuetype.multisz"),
    DWord("DWord", "registry.valuetype.dword"),
    QWord("QWord", "registry.valuetype.qword"),
    Binary("Binary", "registry.valuetype.binary"),
    ;

    public static IssRegistryValueType fromId(final String id) {
        return IssPropertyValue.findById(id, IssRegistryValueType.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssRegistryValueType(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssRegistryValueType(String id, String descriptionKey, boolean deprecated) {
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
