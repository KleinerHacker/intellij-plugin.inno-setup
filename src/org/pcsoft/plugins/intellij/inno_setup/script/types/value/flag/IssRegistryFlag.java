package org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssRegistryFlag implements IssPropertyValue {
    CreateValueIfDoesNotExist("CreateValueIfDoesntExist", "registry.flag.createvalueifdoesntexist"),
    DeleteKey("DeleteKey", "registry.flag.deletekey"),
    DeleteValue("DeleteValue", "registry.flag.deletevalue"),
    DoNotCreateValue("DontCreateValue", "registry.flag.dontcreatekey"),
    NoError("NoError", "registry.flag.noerror"),
    PreserveStringType("PreserveStringType", "registry.flag.preservestringtype"),
    UninstallClearValue("UninsClearValue", "registry.flag.uninsclearvalue"),
    UninstallDeleteKey("UninsDeleteKey", "registry.flag.uninsdeletekey"),
    UninstallDeleteKeyIfEmpty("UninsDeleteKeyIfEmpty", "registry.flag.uninsdeletekeyifempty"),
    UninstallDeleteValue("UninsDeleteValue", "registry.flag.uninsdeletevalue"),
    ;

    public static IssRegistryFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssRegistryFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssRegistryFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssRegistryFlag(String id, String descriptionKey, boolean deprecated) {
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
