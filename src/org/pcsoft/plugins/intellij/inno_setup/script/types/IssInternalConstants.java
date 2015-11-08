package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssInternalConstants implements IssFlag {
    App("app", "constant.internal.app"),
    Win("win", "constant.internal.win"),
    Sys("sys", "constant.internal.sys"),
    ;

    public static IssInternalConstants fromId(final String id) {
        return IssFlag.findById(id, IssInternalConstants.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssInternalConstants(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssInternalConstants(String id, String descriptionKey, boolean deprecated) {
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
