package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;

/**
 * Created by Christoph on 14.12.2014.
 */
public final class IssMarkerFactory {

    private static final Language LANGUAGE = IssLanguage.INSTANCE;

    public static final IElementType ISS_FILE = new IElementType("ISS_FILE", LANGUAGE);

    public static final IElementType COMPILER_DIRECTIVE_SYMBOL_SECTION = new IElementType("COMPILER_DIRECTIVE_SYMBOL_SECTION", LANGUAGE);
    public static final IElementType COMPILER_DIRECTIVE_SYMBOL_IDENTIFIER_PARAMETER = new IElementType("COMPILER_DIRECTIVE_SYMBOL_IDENTIFIER_PARAMETER", LANGUAGE);
    public static final IElementType COMPILER_DIRECTIVE_SYMBOL_VALUE_PARAMETER = new IElementType("COMPILER_DIRECTIVE_SYMBOL_VALUE_PARAMETER", LANGUAGE);
    public static final IElementType COMPILER_DIRECTIVE = new IElementType("COMPILER_DIRECTIVE", LANGUAGE);
    public static final IElementType COMPILER_DIRECTIVE_PARAMETERS = new IElementType("COMPILER_DIRECTIVE_PARAMETERS", LANGUAGE);

    public static final IElementType IDENTIFIER = new IElementType("IDENTIFIER", LANGUAGE);
    public static final IElementType IDENTIFIER_REFERENCE = new IElementType("IDENTIFIER_REFERENCE", LANGUAGE);
    public static final IElementType IDENTIFIER_NAME = new IElementType("IDENTIFIER_NAME", LANGUAGE);
    public static final IElementType VALUE = new IElementType("VALUE", LANGUAGE);
    public static final IElementType STRING = new IElementType("STRING", LANGUAGE);

    public static final IElementType BUILTIN_CONSTANT = new IElementType("BUILTIN_CONSTANT", LANGUAGE);
    public static final IElementType MESSAGE_CONSTANT = new IElementType("MESSAGE_CONSTANT", LANGUAGE);
    public static final IElementType COMPILER_DIRECTIVE_CONSTANT = new IElementType("COMPILER_DIRECTIVE_CONSTANT", LANGUAGE);
    public static final IElementType ENVIRONMENT_CONSTANT = new IElementType("COMPILER_DIRECTIVE_CONSTANT", LANGUAGE);
    public static final IElementType CONSTANT_NAME = new IElementType("CONSTANT_NAME", LANGUAGE);
    public static final IElementType CONSTANT_TYPE = new IElementType("CONSTANT_TYPE", LANGUAGE);
    public static final IElementType CONSTANT_ARGUMENTS = new IElementType("CONSTANT_ARGUMENTS", LANGUAGE);
    public static final IElementType CONSTANT_ARGUMENT = new IElementType("CONSTANT_ARGUMENT", LANGUAGE);

    public static final IElementType SECTION_NAME = new IElementType("SECTION_NAME", LANGUAGE);
    public static final IElementType SECTION_HEADER = new IElementType("SECTION_HEADER", LANGUAGE);

    public static final IElementType PROPERTY_UNKNOWN = new IElementType("PROPERTY_UNKNOWN", LANGUAGE);
    public static final IElementType SECTION_UNKNOWN = new IElementType("SECTION_UNKNOWN", LANGUAGE);

    //region Common
    public static final class CommonSection {
        private static final String KEY = "COMMON_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType PROPERTY_LANGUAGES = new IElementType(KEY_ITEM + "_LANGUAGES", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGES_VALUE = new IElementType(KEY_ITEM + "_LANGUAGES_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_MINVERSION = new IElementType(KEY_ITEM + "_MINVERSION", LANGUAGE);
        public static final IElementType PROPERTY_MINVERSION_VALUE = new IElementType(KEY_ITEM + "_MINVERSION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ONLYBELOWVERSION = new IElementType(KEY_ITEM + "_ONLYBELOWVERSION", LANGUAGE);
        public static final IElementType PROPERTY_ONLYBELOWVERSION_VALUE = new IElementType(KEY_ITEM + "_ONLYBELOWVERSION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType PROPERTY_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
    }
    //endregion

    //region Standard Sections
    public static final class SetupSection {
        private static final String KEY = "SETUP_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType PROPERTY_APPNAME = new IElementType(KEY_ITEM + "_APPNAME", LANGUAGE);
        public static final IElementType PROPERTY_APPNAME_VALUE = new IElementType(KEY_ITEM + "_APPNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_APPVERSION = new IElementType(KEY_ITEM + "_APPVERSION", LANGUAGE);
        public static final IElementType PROPERTY_APPVERSION_VALUE = new IElementType(KEY_ITEM + "_APPVERSION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPRESSION = new IElementType(KEY_ITEM + "_COMPRESSION", LANGUAGE);
        public static final IElementType PROPERTY_COMPRESSION_VALUE = new IElementType(KEY_ITEM + "_COMPRESSION_VALUE", LANGUAGE);
    }

    public static final class LanguageOptionSection {
        private static final String KEY = "LANGUAGEOPTION_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGENAME = new IElementType(KEY_ITEM + "_LANGUAGENAME", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGENAME_VALUE = new IElementType(KEY_ITEM + "_LANGUAGENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGEID = new IElementType(KEY_ITEM + "_LANGUAGEID", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGEID_VALUE = new IElementType(KEY_ITEM + "_LANGUAGEID_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGECODEPAGE = new IElementType(KEY_ITEM + "_LANGUAGECODEPAGE", LANGUAGE);
        public static final IElementType PROPERTY_LANGUAGECODEPAGE_VALUE = new IElementType(KEY_ITEM + "_LANGUAGECODEPAGE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DIALOGFONTNAME = new IElementType(KEY_ITEM + "_LANGUAGECODEPAGE", LANGUAGE);
        public static final IElementType PROPERTY_DIALOGFONTNAME_VALUE = new IElementType(KEY_ITEM + "_DIALOGFONTNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DIALOGFONTSIZE = new IElementType(KEY_ITEM + "_DIALOGFONTSIZE", LANGUAGE);
        public static final IElementType PROPERTY_DIALOGFONTSIZE_VALUE = new IElementType(KEY_ITEM + "_DIALOGFONTSIZE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_WELCOMEFONTNAME = new IElementType(KEY_ITEM + "_WELCOMEFONTNAME", LANGUAGE);
        public static final IElementType PROPERTY_WELCOMEFONTNAME_VALUE = new IElementType(KEY_ITEM + "_WELCOMEFONTNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_WELCOMEFONTSIZE = new IElementType(KEY_ITEM + "_WELCOMEFONTSIZE", LANGUAGE);
        public static final IElementType PROPERTY_WELCOMEFONTSIZE_VALUE = new IElementType(KEY_ITEM + "_WELCOMEFONTSIZE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_TITLEFONTNAME = new IElementType(KEY_ITEM + "_TITLEFONTNAME", LANGUAGE);
        public static final IElementType PROPERTY_TITLEFONTNAME_VALUE = new IElementType(KEY_ITEM + "_TITLEFONTNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_TITLEFONTSIZE = new IElementType(KEY_ITEM + "_TITLEFONTSIZE", LANGUAGE);
        public static final IElementType PROPERTY_TITLEFONTSIZE_VALUE = new IElementType(KEY_ITEM + "_TITLEFONTSIZE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COPYRIGHTFONTNAME = new IElementType(KEY_ITEM + "_COPYRIGHTFONTNAME", LANGUAGE);
        public static final IElementType PROPERTY_COPYRIGHTFONTNAME_VALUE = new IElementType(KEY_ITEM + "_COPYRIGHTFONTNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COPYRIGHTFONTSIZE = new IElementType(KEY_ITEM + "_COPYRIGHTFONTSIZE", LANGUAGE);
        public static final IElementType PROPERTY_COPYRIGHTFONTSIZE_VALUE = new IElementType(KEY_ITEM + "_COPYRIGHTFONTSIZE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_RIGHTTOLEFT = new IElementType(KEY_ITEM + "_RIGHTTOLEFT", LANGUAGE);
        public static final IElementType PROPERTY_RIGHTTOLEFT_VALUE = new IElementType(KEY_ITEM + "_RIGHTTOLEFT_VALUE", LANGUAGE);
    }
    //endregion

    //region Definable Sections
    public static final class CustomMessageSection {
        private static final String KEY = "CUSTOMMESSAGE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType PROPERTY_VALUE = new IElementType(KEY_ITEM + "_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VALUE_VALUE = new IElementType(KEY_ITEM + "_VALUE_VALUE", LANGUAGE);
    }

    public static final class MessageSection {
        private static final String KEY = "MESSAGE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType PROPERTY_VALUE = new IElementType(KEY_ITEM + "_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VALUE_VALUE = new IElementType(KEY_ITEM + "_VALUE_VALUE", LANGUAGE);
    }

    public static final class TaskSection {
        private static final String KEY = "TASK_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_GROUPDESCRIPTION = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_GROUPDESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class IconSection {
        private static final String KEY = "ICON_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME = new IElementType(KEY_ITEM + "_FILENAME", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME_VALUE = new IElementType(KEY_ITEM + "_FILENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS = new IElementType(KEY_ITEM + "_PARAMETERS", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS_VALUE = new IElementType(KEY_ITEM + "_PARAMETERS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR = new IElementType(KEY_ITEM + "_WORKINGDIR", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR_VALUE = new IElementType(KEY_ITEM + "_WORKINGDIR_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_HOTKEY = new IElementType(KEY_ITEM + "_HOTKEY", LANGUAGE);
        public static final IElementType PROPERTY_HOTKEY_VALUE = new IElementType(KEY_ITEM + "_HOTKEY_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMMENT = new IElementType(KEY_ITEM + "_COMMENT", LANGUAGE);
        public static final IElementType PROPERTY_COMMENT_VALUE = new IElementType(KEY_ITEM + "_COMMENT_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ICONFILENAME = new IElementType(KEY_ITEM + "_ICONFILENAME", LANGUAGE);
        public static final IElementType PROPERTY_ICONFILENAME_VALUE = new IElementType(KEY_ITEM + "_ICONFILENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ICONINDEX = new IElementType(KEY_ITEM + "_ICONINDEX", LANGUAGE);
        public static final IElementType PROPERTY_ICONINDEX_VALUE = new IElementType(KEY_ITEM + "_ICONINDEX_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_APPUSERMODELID = new IElementType(KEY_ITEM + "_APPUSERMODELID", LANGUAGE);
        public static final IElementType PROPERTY_APPUSERMODELID_VALUE = new IElementType(KEY_ITEM + "_APPUSERMODELID_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class InstallRunSection {
        private static final String KEY = "INSTALL_RUN_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME = new IElementType(KEY_ITEM + "_FILENAME", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME_VALUE = new IElementType(KEY_ITEM + "_FILENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS = new IElementType(KEY_ITEM + "_PARAMETERS", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS_VALUE = new IElementType(KEY_ITEM + "_PARAMETERS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR = new IElementType(KEY_ITEM + "_WORKINGDIR", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR_VALUE = new IElementType(KEY_ITEM + "_WORKINGDIR_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_STATUSMSG = new IElementType(KEY_ITEM + "_STATUSMSG", LANGUAGE);
        public static final IElementType PROPERTY_STATUSMSG_VALUE = new IElementType(KEY_ITEM + "_STATUSMSG_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VERB = new IElementType(KEY_ITEM + "_VERB", LANGUAGE);
        public static final IElementType PROPERTY_VERB_VALUE = new IElementType(KEY_ITEM + "_VERB_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class UninstallRunSection {
        private static final String KEY = "UNINSTALL_RUN_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME = new IElementType(KEY_ITEM + "_FILENAME", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME_VALUE = new IElementType(KEY_ITEM + "_FILENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS = new IElementType(KEY_ITEM + "_PARAMETERS", LANGUAGE);
        public static final IElementType PROPERTY_PARAMETERS_VALUE = new IElementType(KEY_ITEM + "_PARAMETERS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR = new IElementType(KEY_ITEM + "_WORKINGDIR", LANGUAGE);
        public static final IElementType PROPERTY_WORKINGDIR_VALUE = new IElementType(KEY_ITEM + "_WORKINGDIR_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_RUNONCEID = new IElementType(KEY_ITEM + "_RUNONCEID", LANGUAGE);
        public static final IElementType PROPERTY_RUNONCEID_VALUE = new IElementType(KEY_ITEM + "_RUNONCEID_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VERB = new IElementType(KEY_ITEM + "_VERB", LANGUAGE);
        public static final IElementType PROPERTY_VERB_VALUE = new IElementType(KEY_ITEM + "_VERB_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class FileSection {
        private static final String KEY = "FILE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_SOURCE = new IElementType(KEY_ITEM + "_SOURCE", LANGUAGE);
        public static final IElementType PROPERTY_SOURCE_VALUE = new IElementType(KEY_ITEM + "_SOURCE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESTDIR = new IElementType(KEY_ITEM + "_DESTDIR", LANGUAGE);
        public static final IElementType PROPERTY_DESTDIR_VALUE = new IElementType(KEY_ITEM + "_DESTDIR_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COPYMODE = new IElementType(KEY_ITEM + "_COPYMODE", LANGUAGE);
        public static final IElementType PROPERTY_COPYMODE_VALUE = new IElementType(KEY_ITEM + "_COPYMODE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE = new IElementType(KEY_ITEM + "_ATTRIBUTE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE_VALUE = new IElementType(KEY_ITEM + "_ATTRIBUTE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESTNAME = new IElementType(KEY_ITEM + "_DESTNAME", LANGUAGE);
        public static final IElementType PROPERTY_DESTNAME_VALUE = new IElementType(KEY_ITEM + "_DESTNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_EXCLUDES = new IElementType(KEY_ITEM + "_EXCLUDES", LANGUAGE);
        public static final IElementType PROPERTY_EXCLUDES_VALUE = new IElementType(KEY_ITEM + "_EXCLUDES_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_EXTERNALSIZE = new IElementType(KEY_ITEM + "_EXTERNALSIZE", LANGUAGE);
        public static final IElementType PROPERTY_EXTERNALSIZE_VALUE = new IElementType(KEY_ITEM + "_EXTERNAL_SIZE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FONTINSTALL = new IElementType(KEY_ITEM + "_FONTINSTALL", LANGUAGE);
        public static final IElementType PROPERTY_FONTINSTALL_VALUE = new IElementType(KEY_ITEM + "_FONTINSTALL_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_STRONGASSEMBLYNAME = new IElementType(KEY_ITEM + "_STRONGASSEMBLYNAME", LANGUAGE);
        public static final IElementType PROPERTY_STRONGASSEMBLYNAME_VALUE = new IElementType(KEY_ITEM + "_STRONGASSEMBLYNAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class DirectorySection {
        private static final String KEY = "DIRECTORY_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE = new IElementType(KEY_ITEM + "_ATTRIBUTE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE_VALUE = new IElementType(KEY_ITEM + "_ATTRIBUTE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class ComponentSection {
        private static final String KEY = "COMPONENT_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_EXTRADISKSPACEREQUIRED = new IElementType(KEY_ITEM + "_EXTRADISKSPACEREQUIRED", LANGUAGE);
        public static final IElementType PROPERTY_EXTRADISKSPACEREQUIRED_VALUE = new IElementType(KEY_ITEM + "_EXTRADISKSPACEREQUIRED_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_TYPES = new IElementType(KEY_ITEM + "_TYPES", LANGUAGE);
        public static final IElementType PROPERTY_TYPES_VALUE = new IElementType(KEY_ITEM + "_TYPES_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class TypeSection {
        private static final String KEY = "TYPE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class LanguageSection {
        private static final String KEY = "LANGUAGE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_MEESAGEFILE = new IElementType(KEY_ITEM + "_MEESAGEFILE", LANGUAGE);
        public static final IElementType PROPERTY_MEESAGEFILE_VALUE = new IElementType(KEY_ITEM + "_MEESAGEFILE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_LICENCEFILE = new IElementType(KEY_ITEM + "_LICENCEFILE", LANGUAGE);
        public static final IElementType PROPERTY_LICENCEFILE_VALUE = new IElementType(KEY_ITEM + "_LICENCEFILE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_INFOBEFOREFILE = new IElementType(KEY_ITEM + "_INFOBEFOREFILE", LANGUAGE);
        public static final IElementType PROPERTY_INFOBEFOREFILE_VALUE = new IElementType(KEY_ITEM + "_INFOBEFOREFILE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_INFOAFTERFILE = new IElementType(KEY_ITEM + "_INFOAFTERFILE", LANGUAGE);
        public static final IElementType PROPERTY_INFOAFTERFILE_VALUE = new IElementType(KEY_ITEM + "_INFOAFTERFILE_VALUE", LANGUAGE);
    }

    public static final class INISection {
        private static final String KEY = "INI_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME = new IElementType(KEY_ITEM + "_FILENAME", LANGUAGE);
        public static final IElementType PROPERTY_FILENAME_VALUE = new IElementType(KEY_ITEM + "_FILENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_SECTION = new IElementType(KEY_ITEM + "_SECTION", LANGUAGE);
        public static final IElementType PROPERTY_SECTION_VALUE = new IElementType(KEY_ITEM + "_SECTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_KEY = new IElementType(KEY_ITEM + "_KEY", LANGUAGE);
        public static final IElementType PROPERTY_KEY_VALUE = new IElementType(KEY_ITEM + "_KEY_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_STRING = new IElementType(KEY_ITEM + "_STRING", LANGUAGE);
        public static final IElementType PROPERTY_STRING_VALUE = new IElementType(KEY_ITEM + "_STRING_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class InstallDeleteSection {
        private static final String KEY = "INSTALLDELETE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_TYPE = new IElementType(KEY_ITEM + "_TYPE", LANGUAGE);
        public static final IElementType PROPERTY_TYPE_VALUE = new IElementType(KEY_ITEM + "_TYPE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
    }

    public static final class UninstallDeleteSection {
        private static final String KEY = "UNINSTALLDELETE_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_TYPE = new IElementType(KEY_ITEM + "_TYPE", LANGUAGE);
        public static final IElementType PROPERTY_TYPE_VALUE = new IElementType(KEY_ITEM + "_TYPE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
    }

    public static final class RegistrySection {
        private static final String KEY = "REGISTRY_SECTION";
        private static final String KEY_ITEM = KEY + "_PROPERTY";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_ROOT = new IElementType(KEY_ITEM + "_ROOT", LANGUAGE);
        public static final IElementType PROPERTY_ROOT_VALUE = new IElementType(KEY_ITEM + "_ROOT_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_SUBKEY = new IElementType(KEY_ITEM + "_SUBKEY", LANGUAGE);
        public static final IElementType PROPERTY_SUBKEY_VALUE = new IElementType(KEY_ITEM + "_SUBKEY_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VALUETYPE = new IElementType(KEY_ITEM + "_VALUETYPE", LANGUAGE);
        public static final IElementType PROPERTY_VALUETYPE_VALUE = new IElementType(KEY_ITEM + "_VALUETYPE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VALUENAME = new IElementType(KEY_ITEM + "_VALUENAME", LANGUAGE);
        public static final IElementType PROPERTY_VALUENAME_VALUE = new IElementType(KEY_ITEM + "_VALUENAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_VALUEDATA = new IElementType(KEY_ITEM + "_VALUEDATA", LANGUAGE);
        public static final IElementType PROPERTY_VALUEDATA_VALUE = new IElementType(KEY_ITEM + "_VALUEDATA_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }
    //endregion

    private IssMarkerFactory() {
    }
}
