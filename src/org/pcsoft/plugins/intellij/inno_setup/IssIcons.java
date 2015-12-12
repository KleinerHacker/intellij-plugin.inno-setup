package org.pcsoft.plugins.intellij.inno_setup;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;
import java.util.Locale;

/**
 * Created by Christoph on 16.12.2014.
 */
public final class IssIcons {

    public static final Icon INNO_SETUP = IconLoader.findIcon("/icons/is.png");
    public static final Icon INNO_SETUP_HELP = IconLoader.findIcon("/icons/is_help.png");
    public static final Icon IS_SCRIPT = IconLoader.findIcon("/icons/is_script.png");
    public static final Icon IS_SCRIPT_BIG = IconLoader.findIcon("/icons/is_script_big.png");
    public static final Icon IS_CMD = IconLoader.findIcon("/icons/is_cmd.png");

    public static final Icon IC_REF_COMPONENT = IconLoader.findIcon("/icons/ic_sect_component_ref.png");
    public static final Icon IC_REF_TASK = IconLoader.findIcon("/icons/ic_sect_task_ref.png");
    public static final Icon IC_REF_TYPE = IconLoader.findIcon("/icons/ic_sect_type_ref.png");

    public static final Icon IC_SECT_COMPONENT = IconLoader.findIcon("/icons/ic_sect_component.png");
    public static final Icon IC_SECT_TASK = IconLoader.findIcon("/icons/ic_sect_task.png");
    public static final Icon IC_SECT_TYPE = IconLoader.findIcon("/icons/ic_sect_type.png");
    public static final Icon IC_SECT_FILE = IconLoader.findIcon("/icons/ic_sect_file.png");
    public static final Icon IC_SECT_DIRECTORY = IconLoader.findIcon("/icons/ic_sect_directory.png");
    public static final Icon IC_SECT_SETUP = IconLoader.findIcon("/icons/ic_sect_setup.png");
    public static final Icon IC_SECT_ICON = IconLoader.findIcon("/icons/ic_sect_icon.png");
    public static final Icon IC_SECT_INSTALL_RUN = IconLoader.findIcon("/icons/ic_sect_install_run.png");
    public static final Icon IC_SECT_UNINSTALL_RUN = IconLoader.findIcon("/icons/ic_sect_uninstall_run.png");
    public static final Icon IC_SECT_INI = IconLoader.findIcon("/icons/ic_sect_ini.png");
    public static final Icon IC_SECT_REGISTRY = IconLoader.findIcon("/icons/ic_sect_registry.png");
    public static final Icon IC_SECT_MESSAGES = IconLoader.findIcon("/icons/ic_sect_messages.png");
    public static final Icon IC_SECT_CUSTOM_MESSAGES = IconLoader.findIcon("/icons/ic_sect_custom_messages.png");
    public static final Icon IC_SECT_LANGUAGE_OPTION = IconLoader.findIcon("/icons/ic_sect_language_option.png");
    public static final Icon IC_SECT_LANGUAGE = IconLoader.findIcon("/icons/ic_sect_language.png");
    public static final Icon IC_SECT_INSTALL_DELETE = IconLoader.findIcon("/icons/ic_sect_install_delete.png");
    public static final Icon IC_SECT_UNINSTALL_DELETE = IconLoader.findIcon("/icons/ic_sect_uninstall_delete.png");

    public static final Icon IC_INFO_FLAG = IconLoader.findIcon("/icons/ic_info_flag.png");
    public static final Icon IC_INFO_ATTRIBUTE = IconLoader.findIcon("/icons/ic_info_attribute.png");
    public static final Icon IC_INFO_CONSTANT = IconLoader.findIcon("/icons/ic_info_constant.png");
    public static final Icon IC_INFO_PERMISSION = IconLoader.findIcon("/icons/ic_info_permission.png");
    public static final Icon IC_INFO_COPYMODE = IconLoader.findIcon("/icons/ic_info_copymode.png");

    public static Icon findIconByLocale(Locale locale) {
        Icon icon = IconLoader.findIcon("/icons/lan/" + locale.getCountry().toLowerCase() + ".png");
        if (icon == null) {
            icon = IconLoader.findIcon("/icons/lan/" + locale.getLanguage().toLowerCase() + ".png");
        }
        return icon;
    }

    private IssIcons() {
    }
}
