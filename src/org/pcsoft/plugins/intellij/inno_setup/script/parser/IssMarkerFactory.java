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

    public static final IElementType COMPILER_DIRECTIVE_SECTION = new IElementType("COMPILER_DIRECTIVE_SECTION", LANGUAGE);

    public static final IElementType IDENTIFIER = new IElementType("IDENTIFIER", LANGUAGE);
    public static final IElementType VALUE = new IElementType("VALUE", LANGUAGE);

    public static final IElementType SECTION_TITLE = new IElementType("SECTION_TITLE", LANGUAGE);

    public static final IElementType PROPERTY_UNKNOWN = new IElementType("PROPERTY_UNKNOWN", LANGUAGE);

    public static final class SetupSection {
        private static final String KEY = "SETUP_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType PROPERTY_APP_NAME = new IElementType(KEY_ITEM + "_APP_NAME", LANGUAGE);
        public static final IElementType PROPERTY_APP_VERSION = new IElementType(KEY_ITEM + "_APP_VERSION", LANGUAGE);
    }

    public static final class TaskSection {
        private static final String KEY = "TASK_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_GROUPDESCRIPTION = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_GROUPDESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class IconSection {
        private static final String KEY = "ICON_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

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
        public static final IElementType PROPERTY_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType PROPERTY_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class FileSection {
        private static final String KEY = "FILE_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

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
        public static final IElementType PROPERTY_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType PROPERTY_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class DirectorySection {
        private static final String KEY = "DIRECTORY_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE = new IElementType(KEY_ITEM + "_ATTRIBUTE", LANGUAGE);
        public static final IElementType PROPERTY_ATTRIBUTE_VALUE = new IElementType(KEY_ITEM + "_ATTRIBUTE_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType PROPERTY_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType PROPERTY_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType PROPERTY_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class ComponentSection {
        private static final String KEY = "COMPONENT_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

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
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType PROPERTY_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType PROPERTY_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType PROPERTY_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType PROPERTY_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    private IssMarkerFactory() {
    }
}
