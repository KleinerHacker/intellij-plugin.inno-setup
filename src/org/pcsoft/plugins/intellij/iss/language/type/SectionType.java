package org.pcsoft.plugins.intellij.iss.language.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;
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
    Setup("Setup", IssIcons.Sections.Setup, SectionTypeVariant.Default, SetupPropertyType.class),
    Files("Files", IssIcons.Sections.Files, SectionTypeVariant.LineBased, FilesPropertyType.class),
    Directories("Dirs", IssIcons.Sections.Directories, SectionTypeVariant.LineBased, DirsPropertyType.class),
    Types("Types", IssIcons.Sections.Types, SectionTypeVariant.LineBased, TypesPropertyType.class),
    Components("Components", IssIcons.Sections.Components, SectionTypeVariant.LineBased, ComponentsPropertyType.class),
    Tasks("Tasks", IssIcons.Sections.Tasks, SectionTypeVariant.LineBased, TasksPropertyType.class),
    Icons("Icons", IssIcons.Sections.Icons, SectionTypeVariant.LineBased, IconsPropertyType.class),
    INI("INI", IssIcons.Sections.INI, SectionTypeVariant.LineBased, INIPropertyType.class),
    Languages("Languages", IssIcons.Sections.Languages, SectionTypeVariant.LineBased, LanguagesPropertyType.class),
    Messages("Messages", IssIcons.Sections.Messages, SectionTypeVariant.Default, EmptyPropertyType.class),
    CustomMessages("CustomMessages", IssIcons.Sections.CustomMessages, SectionTypeVariant.Default, EmptyPropertyType.class),
    LangOptions("LangOptions", IssIcons.Sections.LangOptions, SectionTypeVariant.Default, LangOptionsPropertyType.class),
    Registry("Registry", IssIcons.Sections.Registry, SectionTypeVariant.LineBased, RegistryPropertyType.class),
    InstallDelete("InstallDelete", IssIcons.Sections.InstallDelete, SectionTypeVariant.LineBased, UnInstallDeletePropertyType.class),
    UninstallDelete("UninstallDelete", IssIcons.Sections.UninstallDelete, SectionTypeVariant.LineBased, UnInstallDeletePropertyType.class),
    InstallRun("Run", IssIcons.Sections.InstallRun, SectionTypeVariant.LineBased, InstallRunPropertyType.class),
    UninstallRun("UninstallRun", IssIcons.Sections.UninstallRun, SectionTypeVariant.LineBased, UninstallRunPropertyType.class),
    Code("Code", null, SectionTypeVariant.Code, EmptyPropertyType.class),
    ;

    @NotNull
    private final String name;
    @Nullable
    private final Icon icon;
    @NotNull
    private final SectionTypeVariant variant;
    @NotNull
    private final Class<? extends PropertyType> sectionPropertyClass;
    private final boolean required, deprecated;

    @Nullable
    public static SectionType fromName(@Nullable final String name) {
        if (name == null)
            return null;

        return Stream.of(values())
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private SectionType(@NotNull String name, @Nullable Icon icon, @NotNull SectionTypeVariant variant, @NotNull Class<? extends PropertyType> sectionPropertyClass) {
        this.name = name;
        this.icon = icon;
        this.variant = variant;
        this.sectionPropertyClass = sectionPropertyClass;

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
    public Class<? extends PropertyType> getSectionPropertyClass() {
        return sectionPropertyClass;
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
