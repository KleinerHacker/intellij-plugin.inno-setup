package org.pcsoft.plugins.intellij.iss.language.type;

import org.jetbrains.annotations.Nullable;

public enum PreprocType {
    Builtin("builtin"),
    InnoSetup("ispp"),
    ;

    @Nullable
    public static PreprocType fromName(@Nullable final String name) {
        if (name == null)
            return null;

        for (final PreprocType preprocType : values()) {
            if (preprocType.getName().equalsIgnoreCase(name))
                return preprocType;
        }

        return null;
    }

    private final String name;

    PreprocType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
