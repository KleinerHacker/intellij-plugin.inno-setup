package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

import javax.swing.*;

public enum IssSectionType implements IssSectionIdentifier {
    Setup("Setup", IssMarkerFactory.SetupSection.SECTION, "section.setup", IssIcons.IC_SECT_SETUP, IssFileType.Script, true),
    Code("Code", null, "section.code", null, IssFileType.Script),
    Task("Tasks", IssMarkerFactory.TaskSection.SECTION, "section.tasks", IssIcons.IC_SECT_TASK, IssFileType.Script),
    File("Files", IssMarkerFactory.FileSection.SECTION, "section.files", IssIcons.IC_SECT_FILE, IssFileType.Script),
    Type("Types", IssMarkerFactory.TypeSection.SECTION, "section.types", IssIcons.IC_SECT_TYPE, IssFileType.Script),
    Component("Components", IssMarkerFactory.ComponentSection.SECTION, "section.components", IssIcons.IC_SECT_COMPONENT, IssFileType.Script),
    Directory("Dirs", IssMarkerFactory.DirectorySection.SECTION, "section.dirs", IssIcons.IC_SECT_DIRECTORY, IssFileType.Script),
    Icon("Icons", IssMarkerFactory.IconSection.SECTION, "section.icons", IssIcons.IC_SECT_ICON, IssFileType.Script),
    INI("INI", IssMarkerFactory.INISection.SECTION, "section.ini", IssIcons.IC_SECT_INI, IssFileType.Script),
    InstallDelete("InstallDelete", null, "section.install_delete", null, IssFileType.Script),
    Language("Languages", null, "section.languages", null, IssFileType.Script),
    Message("Messages", IssMarkerFactory.MessageSection.SECTION, "section.messages", IssIcons.IC_SECT_MESSAGES),
    CustomMessage("CustomMessages", IssMarkerFactory.CustomMessageSection.SECTION, "section.custom_messages", IssIcons.IC_SECT_CUSTOM_MESSAGES),
    LanguageOption("LangOptions", IssMarkerFactory.LanguageOptionSection.SECTION, "section.lang_options", IssIcons.IC_SECT_LANGUAGE_OPTION, IssFileType.Language),
    Registry("Registry", IssMarkerFactory.RegistrySection.SECTION, "section.registry", IssIcons.IC_SECT_REGISTRY, IssFileType.Script),
    InstallRun("Run", IssMarkerFactory.InstallRunSection.SECTION, "section.install_run", IssIcons.IC_SECT_INSTALL_RUN, IssFileType.Script),
    UninstallDelete("UninstallDelete", null, "section.uninstall_delete", null, IssFileType.Script),
    UninstallRun("UninstallRun", IssMarkerFactory.UninstallRunSection.SECTION, "section.uninstall_run", IssIcons.IC_SECT_UNINSTALL_RUN, IssFileType.Script);

    public static IssSectionType fromId(final String id) {
        final String preparedId = id.replace("[", "").replace("]", "");
        return IssSectionIdentifier.fromId(preparedId, IssSectionType.class);
    }

    public static IElementType getMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssSectionType.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated, required;
    private final IElementType markerElement;
    private final javax.swing.Icon icon;
    private final IssFileType fileType;

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon) {
        this(id, markerElement, descriptionKey, icon, null);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon, IssFileType fileType) {
        this(id, markerElement, descriptionKey, icon, fileType, false);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon, IssFileType fileType, boolean required) {
        this(id, markerElement, descriptionKey, icon, fileType, required, false);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon, IssFileType fileType, boolean required, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.required = required;
        this.descriptionKey = descriptionKey;
        this.markerElement = markerElement;
        this.icon = icon;
        this.fileType = fileType;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    @NotNull
    @Override
    public IElementType getPropertyMarkerElement() {
        return markerElement;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Nullable
    public javax.swing.Icon getIcon() {
        return icon;
    }

    @Nullable
    public IssFileType getFileType() {
        return fileType;
    }
}