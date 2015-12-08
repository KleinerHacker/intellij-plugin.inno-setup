package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value;

import org.jetbrains.annotations.NotNull;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public interface IssCompilerDirectiveValueIdentifier {
    static <T extends IssCompilerDirectiveValueIdentifier>T fromId(final String id, final Class<T> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException("The class is not an enumeration: " + clazz.getName());

        for (final T item : clazz.getEnumConstants()) {
            if (item.getId().equalsIgnoreCase(id))
                return item;
        }

        return null;
    }

    @NotNull
    String getId();

    @NotNull
    String getDescriptionKey();

    boolean isDeprecated();

}
