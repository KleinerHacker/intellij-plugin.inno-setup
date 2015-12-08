package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value;

import org.jetbrains.annotations.NotNull;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public enum IssCompilerDirectivePreProcessorType implements IssCompilerDirectiveValueIdentifier {
    Builtin("builtin", "cd.preprocessor_type.builtin"),
    ISPP("ispp", "cd.preprocessor_type.ispp"),
    ;

    public static IssCompilerDirectivePreProcessorType fromId(final String id) {
        return IssCompilerDirectiveValueIdentifier.fromId(id, IssCompilerDirectivePreProcessorType.class);
    }

    private final String id;
    private final String descriptionKey;

    private IssCompilerDirectivePreProcessorType(String id, String descriptionKey) {
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
