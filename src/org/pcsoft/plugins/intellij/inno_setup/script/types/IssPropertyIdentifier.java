package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 22.12.2014.
 */
public interface IssPropertyIdentifier {

    static <T extends IssPropertyIdentifier> T fromId(final String id, final Class<T> clazz) {
        if (!clazz.isEnum())
            throw new IllegalArgumentException("The class is not an enumeration: " + clazz.getName());

        for (final T item : clazz.getEnumConstants()) {
            if (item.getId().equalsIgnoreCase(id))
                return item;
        }

        return null;
    }

    static <T extends IssPropertyIdentifier> IElementType getItemMarkerElementFromId(final String id, Class<T> clazz) {
        final T value = fromId(id, clazz);
        if (value == null)
            return null;

        return value.getPropertyMarkerElement();
    }

   static <T extends IssPropertyIdentifier>IElementType getSingleValueMarkerElementFromId(final String id, Class<T> clazz) {
        final T value = IssPropertyIdentifier.fromId(id, clazz);
        if (value == null)
            return null;

        return value.getPropertyValueMarkerElement();
    }

    @NotNull
    String getId();

    @NotNull
    String getDescriptionKey();

    @NotNull
    IElementType getPropertyMarkerElement();

    @Nullable
    IElementType getPropertyValueMarkerElement();

    boolean isRequired();

    boolean isDeprecated();

}
