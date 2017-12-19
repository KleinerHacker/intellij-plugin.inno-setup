package org.pcsoft.plugins.intellij.iss.language.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionValue;
import org.pcsoft.plugins.intellij.iss.language.type.section.SectionDirs;
import org.pcsoft.plugins.intellij.iss.language.type.section.SectionFiles;
import org.pcsoft.plugins.intellij.iss.language.type.section.SectionSetup;
import org.pcsoft.plugins.intellij.iss.language.type.section.SectionTypes;

import javax.swing.*;
import java.util.stream.Stream;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum Section implements SectionType {
    Setup("Setup", IssIcons.Sections.Setup, SectionVariant.SingleValue, SectionSetup.class, true),
    Files("Files", IssIcons.Sections.Files, SectionVariant.MultiValue, SectionFiles.class, false),
    Directories("Dirs", IssIcons.Sections.Directories, SectionVariant.MultiValue, SectionDirs.class, false),
    Types("Types", IssIcons.Sections.Types, SectionVariant.MultiValue, SectionTypes.class, false),
    ;

    @NotNull
    private final String name;
    @Nullable
    private final Icon icon;
    @NotNull
    private final SectionVariant variant;
    @NotNull
    private final Class<? extends SectionValue> sectionValueClass;
    private final boolean required, deprecated;

    @Nullable
    public static Section fromName(@Nullable final String name) {
        if (name == null)
            return null;

        return Stream.of(values())
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private Section(@NotNull String name, @Nullable Icon icon, @NotNull SectionVariant variant, @NotNull Class<? extends SectionValue> sectionValueClass) {
        this(name, icon, variant, sectionValueClass, false);
    }

    private Section(@NotNull String name, @Nullable Icon icon, @NotNull SectionVariant variant, @NotNull Class<? extends SectionValue> sectionValueClass, boolean required) {
        this(name, icon, variant, sectionValueClass, required, false);
    }

    private Section(@NotNull String name, @Nullable Icon icon, @NotNull SectionVariant variant, @NotNull Class<? extends SectionValue> sectionValueClass, boolean required, boolean deprecated) {
        this.name = name;
        this.icon = icon;
        this.variant = variant;
        this.sectionValueClass = sectionValueClass;
        this.required = required;
        this.deprecated = deprecated;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return icon;
    }

    @NotNull
    @Override
    public SectionVariant getVariant() {
        return variant;
    }

    @NotNull
    @Override
    public Class<? extends SectionValue> getSectionValueClass() {
        return sectionValueClass;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
