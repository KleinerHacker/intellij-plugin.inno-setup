package org.pcsoft.plugins.intellij.inno_setup.script.types.value;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssBuiltinConstant implements IssPropertyValue {
    App("app", "constant.internal.app"),
    Win("win", "constant.internal.win"),
    Sys("sys", "constant.internal.sys"),
    ;

    public static IssBuiltinConstant fromId(final String id) {
        return IssPropertyValue.findById(id, IssBuiltinConstant.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssBuiltinConstant(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssBuiltinConstant(String id, String descriptionKey, boolean deprecated) {
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
