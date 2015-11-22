package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssRegistryRoot implements IssPropertyValue {
    HKCR("HKCR", "registry.root.HKCR"),
    HKCU("HKCU", "registry.root.HKCU"),
    HKLM("HKLM", "registry.root.HKLM"),
    HKU("HKU", "registry.root.HKU"),
    HKCC("HKCC", "registry.root.HKCC"),
    ;

    public static IssRegistryRoot fromId(final String id) {
        return IssPropertyValue.findById(id, IssRegistryRoot.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssRegistryRoot(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssRegistryRoot(String id, String descriptionKey, boolean deprecated) {
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
