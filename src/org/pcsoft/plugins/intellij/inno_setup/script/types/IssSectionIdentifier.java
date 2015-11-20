package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 22.12.2014.
 */
public interface IssSectionIdentifier {

    static <T extends IssSectionIdentifier> T fromId(final String id, final Class<T> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException("The class is not an enumeration: " + clazz.getName());

        for (final T item : clazz.getEnumConstants()) {
            if (item.getId().equalsIgnoreCase(id))
                return item;
        }

        return null;
    }

    static <T extends IssSectionIdentifier> IElementType getItemMarkerElementFromId(final String id, Class<T> clazz) {
        final T value = fromId(id, clazz);
        if (value == null)
            return null;

        return value.getPropertyMarkerElement();
    }

    @NotNull
    String getId();

    @NotNull
    String getDescriptionKey();

    @NotNull
    IElementType getPropertyMarkerElement();

    boolean isRequired();

    boolean isDeprecated();

}
