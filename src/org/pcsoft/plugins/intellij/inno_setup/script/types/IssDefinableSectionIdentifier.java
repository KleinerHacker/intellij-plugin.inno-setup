package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 27.12.2014.
 */
public interface IssDefinableSectionIdentifier extends IssSectionIdentifier {

    static <T extends IssDefinableSectionIdentifier>IElementType getSingleValueMarkerElementFromId(final String id, Class<T> clazz) {
        final T value = IssSectionIdentifier.fromId(id, clazz);
        if (value == null)
            return null;

        return value.getValueMarkerElement();
    }

    @Nullable
    IElementType getValueMarkerElement();

    boolean isRequired();

    @NotNull
    IssValueType getValueType();

}
