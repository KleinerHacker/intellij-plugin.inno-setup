package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.awt.Color;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public interface IssLookupElementHint<T> {
    @Nullable
    String getTypeText(T value);

    @Nullable
    String getTailText(T value);

    boolean getBoldness(T value);

    boolean getStrikeout(T value);

    @NotNull
    Color getTextColor(T value);

    @Nullable
    Icon getIcon(T value);
}
