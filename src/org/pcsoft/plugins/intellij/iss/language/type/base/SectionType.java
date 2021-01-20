package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;

import javax.swing.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface SectionType extends TypeBase {
    @Nullable
    Icon getIcon();
    @NotNull
    SectionTypeVariant getVariant();
    @NotNull
    Class<? extends PropertyType> getSectionPropertyClass();

    boolean isRequired();
}
