package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssFileCopyMode implements IssFlag {
    Normal("Normal", ""),
    AlwaysSkipIfSameOrOlder("AlwaysSkipIfSameOrOlder", ""),
    OnlyIfDoesntExists("OnlyIfDoesntExists", ""),
    AlwaysOverwrite("AlwaysOverwrite", ""),
    DontCopy("DontCopy", "");

    public static IssFileCopyMode fromId(final String id) {
        return IssFlag.findById(id, IssFileCopyMode.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssFileCopyMode(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssFileCopyMode(String id, String descriptionKey, boolean deprecated) {
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
