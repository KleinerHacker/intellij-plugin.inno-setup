package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;

public enum IssSectionType implements IssSectionIdentifier {
    Setup("Setup", IssMarkerFactory.SetupSection.SECTION, "section.setup"),
    Code("Code", null, "section.code"),
    Task("Tasks", IssMarkerFactory.TaskSection.SECTION, "section.tasks"),
    File("Files", IssMarkerFactory.FileSection.SECTION, "section.files"),
    Type("Types", IssMarkerFactory.TypeSection.SECTION, "section.types"),
    Component("Components", IssMarkerFactory.ComponentSection.SECTION, "section.components"),
    Directory("Dirs", IssMarkerFactory.DirectorySection.SECTION, "section.dirs"),
    Icon("Icons", null, "section.icons"),
    Ini("INI", null, "section.ini"),
    InstallDelete("InstallDelete", null, "section.install_delete"),
    Language("Languages", null, "section.languages"),
    Message("Messages", null, "section.messages"),
    CustomMessage("CustomMessages", null, "section.custom_messages"),
    LanguageOption("LangOptions", null, "section.lang_options"),
    Registry("Registry", null, "section.registry"),
    InstallRun("Run", null, "section.install_run"),
    UninstallDelete("UninstallDelete", null, "section.uninstall_delete"),
    UninstallRun("UninstallRun", null, "section.uninstall_run");

    public static IssSectionType fromId(final String id) {
        final String preparedId = id.replace("[", "").replace("]", "");
        return IssSectionIdentifier.fromId(preparedId, IssSectionType.class);
    }

    public static IElementType getMarkerElementFromId(final String id) {
        return IssSectionIdentifier.getItemMarkerElementFromId(id, IssSectionType.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;
    private final IElementType markerElement;

    private IssSectionType(String id, IElementType markerElement, String descriptionKey) {
        this(id, markerElement, descriptionKey, false);
    }

    private IssSectionType(String id, IElementType markerElement, String descriptionKey, boolean deprecated) {
        this.id = id;
        this.deprecated = deprecated;
        this.descriptionKey = descriptionKey;
        this.markerElement = markerElement;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Nullable
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
    public IElementType getItemMarkerElement() {
        return markerElement;
    }
}