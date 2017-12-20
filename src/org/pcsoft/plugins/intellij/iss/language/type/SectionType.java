package org.pcsoft.plugins.intellij.iss.language.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsDeprecated;
import org.pcsoft.plugins.intellij.iss.language.type.base.annotation.IsRequired;
import org.pcsoft.plugins.intellij.iss.language.type.section.*;

import javax.swing.*;
import java.util.stream.Stream;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum SectionType implements org.pcsoft.plugins.intellij.iss.language.type.base.SectionType {
    @IsRequired
    Setup("Setup", IssIcons.Sections.Setup, SectionTypeVariant.Default, SectionSetupProperty.class),
    Files("Files", IssIcons.Sections.Files, SectionTypeVariant.LineBased, SectionFilesProperty.class),
    Directories("Dirs", IssIcons.Sections.Directories, SectionTypeVariant.LineBased, SectionDirsProperty.class),
    Types("Types", IssIcons.Sections.Types, SectionTypeVariant.LineBased, SectionTypesProperty.class),
    Components("Components", IssIcons.Sections.Components, SectionTypeVariant.LineBased, SectionComponentsProperty.class),
    Tasks("Tasks", IssIcons.Sections.Tasks, SectionTypeVariant.LineBased, SectionTasksProperty.class),
    Icons("Icons", IssIcons.Sections.Icons, SectionTypeVariant.LineBased, SectionIconsProperty.class),
    INI("INI", IssIcons.Sections.INI, SectionTypeVariant.LineBased, SectionINIProperty.class),
    Languages("Languages", IssIcons.Sections.Languages, SectionTypeVariant.LineBased, SectionLanguagesProperty.class),
    Messages("Messages", IssIcons.Sections.Messages, SectionTypeVariant.Default, SectionMessagesProperty.class),
    CustomMessages("CustomMessages", IssIcons.Sections.CustomMessages, SectionTypeVariant.Default, SectionCustomMessagesProperty.class),
    LangOptions("LangOptions", IssIcons.Sections.LangOptions, SectionTypeVariant.Default, SectionLangOptionsProperty.class),
    Registry("Registry", IssIcons.Sections.Registry, SectionTypeVariant.LineBased, SectionRegistryProperty.class),
    ;

    @NotNull
    private final String name;
    @Nullable
    private final Icon icon;
    @NotNull
    private final SectionTypeVariant variant;
    @NotNull
    private final Class<? extends SectionProperty> sectionValueClass;
    private final boolean required, deprecated;

    @Nullable
    public static SectionType fromName(@Nullable final String name) {
        if (name == null)
            return null;

        return Stream.of(values())
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private SectionType(@NotNull String name, @Nullable Icon icon, @NotNull SectionTypeVariant variant, @NotNull Class<? extends SectionProperty> sectionValueClass) {
        this.name = name;
        this.icon = icon;
        this.variant = variant;
        this.sectionValueClass = sectionValueClass;

        try {
            required = getClass().getField(name()).getAnnotation(IsRequired.class) != null;
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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
    public SectionTypeVariant getVariant() {
        return variant;
    }

    @NotNull
    @Override
    public Class<? extends SectionProperty> getSectionValueClass() {
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
