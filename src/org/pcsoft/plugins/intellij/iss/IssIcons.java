package org.pcsoft.plugins.intellij.iss;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by Christoph on 30.09.2016.
 */
public final class IssIcons {
    public static final Icon FILE = IconLoader.getIcon("/icons/is_script.png");
    public static final Icon FILE_BIG = IconLoader.getIcon("/icons/is_script_big.png");

    public static final class Sections {
        public static final Icon Setup = IconLoader.getIcon("/icons/ic_sect_setup.png");
        public static final Icon Files = IconLoader.getIcon("/icons/ic_sect_file.png");
        public static final Icon Directories = IconLoader.getIcon("/icons/ic_sect_directory.png");
        public static final Icon Types = IconLoader.getIcon("/icons/ic_sect_type.png");
        public static final Icon Components = IconLoader.getIcon("/icons/ic_sect_component.png");
        public static final Icon Tasks = IconLoader.getIcon("/icons/ic_sect_task.png");
        public static final Icon Icons = IconLoader.getIcon("/icons/ic_sect_icon.png");
        public static final Icon INI = IconLoader.getIcon("/icons/ic_sect_ini.png");
        public static final Icon InstallDelete = IconLoader.getIcon("/icons/ic_sect_install_delete.png");
        public static final Icon Languages = IconLoader.getIcon("/icons/ic_sect_language.png");
        public static final Icon Messages = IconLoader.getIcon("/icons/ic_sect_messages.png");
        public static final Icon CustomMessages = IconLoader.getIcon("/icons/ic_sect_custom_messages.png");
        public static final Icon LangOptions = IconLoader.getIcon("/icons/ic_sect_language_option.png");
        public static final Icon Registry = IconLoader.getIcon("/icons/ic_sect_registry.png");
        public static final Icon InstallRun = IconLoader.getIcon("/icons/ic_sect_install_run.png");
        public static final Icon UninstallDelete = IconLoader.getIcon("/icons/ic_sect_uninstall_delete.png");
        public static final Icon UninstallRun = IconLoader.getIcon("/icons/ic_sect_uninstall_run.png");
    }

    public static final class Constants {
        public static final Icon Directory = IconLoader.getIcon("/icons/ic_const_directory.png");
        public static final Icon Shell = IconLoader.getIcon("/icons/ic_const_shell.png");
    }

    private IssIcons() {
    }
}
