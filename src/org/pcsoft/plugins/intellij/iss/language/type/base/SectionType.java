package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.SectionVariant;

import javax.swing.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface SectionType extends SectionBase {
    @NotNull
    String getName();
    @Nullable
    Icon getIcon();
    @NotNull
    SectionVariant getVariant();
    @NotNull
    Class<? extends SectionValue> getSectionValueClass();
}
