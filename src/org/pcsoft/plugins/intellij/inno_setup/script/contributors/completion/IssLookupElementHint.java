package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public interface IssLookupElementHint<T> {
    @NotNull
    TextAttributesKey getTextAttributesKey(T value);

    @NotNull
    String getItemText(T value);

    @Nullable
    String getTypeText(T value);

    @Nullable
    String getTailText(T value);

    @Nullable
    Icon getIcon(T value);
}
