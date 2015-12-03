package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public enum IssConstantType implements IssPropertyValue {
    Builtin(null, "constant.type.builtin"),
    Message("cm", "constant.type.message");

    public static IssConstantType fromId(final String id) {
        return IssPropertyValue.findById(id, IssConstantType.class);
    }

    private final String id, descriptionKey;

    private IssConstantType(String id, String descriptionKey) {
        this.id = id;
        this.descriptionKey = descriptionKey;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }
}
