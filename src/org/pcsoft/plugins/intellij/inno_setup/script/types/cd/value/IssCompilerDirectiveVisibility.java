package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value;

import org.jetbrains.annotations.NotNull;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public enum IssCompilerDirectiveVisibility implements IssCompilerDirectiveValueIdentifier {
    Private("private", "cd.visibility.private"),
    Protected("protected", "cd.visibility.protected"),
    Public("public", "cd.visibility.public"),
    ;

    public static IssCompilerDirectiveVisibility fromId(final String id) {
        return IssCompilerDirectiveValueIdentifier.fromId(id, IssCompilerDirectiveVisibility.class);
    }

    private final String id;
    private final String descriptionKey;

    private IssCompilerDirectiveVisibility(String id, String descriptionKey) {
        this.id = id;
        this.descriptionKey = descriptionKey;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }


}
