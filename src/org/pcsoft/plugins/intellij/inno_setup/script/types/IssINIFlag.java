package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssINIFlag implements IssPropertyValue {
    CreateKeyIfDoesNotExist("CreateKeyIfDoesntExist", "ini.flag.createkeyifdoesntexist"),
    UninstallDeleteEntry("UninsDeleteEntry", "ini.flag.uninsdeleteentry"),
    UninstallDeleteSection("UninsDeleteSection", "ini.flag.uninsdeletesection"),
    UninstallDeleteSectionIfEmpty("UninsDeleteSectionIfEmpty", "ini.flag.uninsdeletesectionifempty")
    ;

    public static IssINIFlag fromId(final String id) {
        return IssPropertyValue.findById(id, IssINIFlag.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssINIFlag(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssINIFlag(String id, String descriptionKey, boolean deprecated) {
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
