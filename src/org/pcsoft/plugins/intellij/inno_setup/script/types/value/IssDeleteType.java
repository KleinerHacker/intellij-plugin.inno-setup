package org.pcsoft.plugins.intellij.inno_setup.script.types.value;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssDeleteType implements IssPropertyValue {
    Files("Files", "common.delete_type.files"),
    FilesAndOrDirectories("FilesAndOrDirs", "common.delete_type.filesandordirs"),
    DirectoryIfEmpty("DirIfEmpty", "common.delete_type.dirifempty")
    ;

    public static IssDeleteType fromId(final String id) {
        return IssPropertyValue.findById(id, IssDeleteType.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssDeleteType(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssDeleteType(String id, String descriptionKey, boolean deprecated) {
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
