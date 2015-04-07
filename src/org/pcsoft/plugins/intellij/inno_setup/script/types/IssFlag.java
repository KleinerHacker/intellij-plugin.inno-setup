package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public interface IssFlag {

    static <T extends IssFlag>T findById(final String id, Class<T> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException("Class must be an enumeration");

        for (final T value : clazz.getEnumConstants()) {
            if (value.getId().equalsIgnoreCase(id))
                return value;
        }

        return null;
    }

    String getId();
    boolean isDeprecated();
    String getDescriptionKey();
}
