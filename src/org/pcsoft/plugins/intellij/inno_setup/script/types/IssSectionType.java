package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

import javax.swing.*;

public enum IssSectionType implements IssSectionIdentifier {
    Setup("Setup", IssMarkerFactory.SetupSection.SECTION, "section.setup", IssIcons.IC_SECT_SETUP),
    Code("Code", null, "section.code", null),
    Task("Tasks", IssMarkerFactory.TaskSection.SECTION, "section.tasks", IssIcons.IC_SECT_TASK),
    File("Files", IssMarkerFactory.FileSection.SECTION, "section.files", IssIcons.IC_SECT_FILE),
    Type("Types", IssMarkerFactory.TypeSection.SECTION, "section.types", IssIcons.IC_SECT_TYPE),
    Component("Components", IssMarkerFactory.ComponentSection.SECTION, "section.components", IssIcons.IC_SECT_COMPONENT),
    Directory("Dirs", IssMarkerFactory.DirectorySection.SECTION, "section.dirs", IssIcons.IC_SECT_DIRECTORY),
    Icon("Icons", IssMarkerFactory.IconSection.SECTION, "section.icons", IssIcons.IC_SECT_ICON),
    Ini("INI", null, "section.ini", null),
    InstallDelete("InstallDelete", null, "section.install_delete", null),
    Language("Languages", null, "section.languages", null),
    Message("Messages", null, "section.messages", null),
    CustomMessage("CustomMessages", null, "section.custom_messages", null),
    LanguageOption("LangOptions", null, "section.lang_options", null),
    Registry("Registry", null, "section.registry", null),
    InstallRun("Run", IssMarkerFactory.InstallRunSection.SECTION, "section.install_run", IssIcons.IC_SECT_INSTALL_RUN),
    UninstallDelete("UninstallDelete", null, "section.uninstall_delete", null),
    UninstallRun("UninstallRun", IssMarkerFactory.UninstallRunSection.SECTION, "section.uninstall_run", IssIcons.IC_SECT_UNINSTALL_RUN);

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

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon) {
        this(id, markerElement, descriptionKey, icon, false);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon, boolean required) {
        this(id, markerElement, descriptionKey, icon, required, false);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, Icon icon, boolean required, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.required = required;
        this.descriptionKey = descriptionKey;
        this.markerElement = markerElement;
        this.icon = icon;
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


}