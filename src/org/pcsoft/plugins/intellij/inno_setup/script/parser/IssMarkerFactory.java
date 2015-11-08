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

    public static final IElementType ITEM_DEFAULT = new IElementType("ITEM_DEFAULT", LANGUAGE);

    public static final class SetupSection {
        private static final String KEY = "SETUP_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType ITEM_APP_NAME = new IElementType(KEY_ITEM + "_APP_NAME", LANGUAGE);
        public static final IElementType ITEM_APP_VERSION = new IElementType(KEY_ITEM + "_APP_VERSION", LANGUAGE);
    }

    public static final class TaskSection {
        private static final String KEY = "TASK_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType ITEM_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType ITEM_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType ITEM_GROUPDESCRIPTION = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION", LANGUAGE);
        public static final IElementType ITEM_GROUPDESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_GROUPDESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType ITEM_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType ITEM_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class FileSection {
        private static final String KEY = "FILE_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType ITEM_SOURCE = new IElementType(KEY_ITEM + "_SOURCE", LANGUAGE);
        public static final IElementType ITEM_SOURCE_VALUE = new IElementType(KEY_ITEM + "_SOURCE_VALUE", LANGUAGE);
        public static final IElementType ITEM_DESTDIR = new IElementType(KEY_ITEM + "_DESTDIR", LANGUAGE);
        public static final IElementType ITEM_DESTDIR_VALUE = new IElementType(KEY_ITEM + "_DESTDIR_VALUE", LANGUAGE);
        public static final IElementType ITEM_COPYMODE = new IElementType(KEY_ITEM + "_COPYMODE", LANGUAGE);
        public static final IElementType ITEM_COPYMODE_VALUE = new IElementType(KEY_ITEM + "_COPYMODE_VALUE", LANGUAGE);
        public static final IElementType ITEM_ATTRIBUTE = new IElementType(KEY_ITEM + "_ATTRIBUTE", LANGUAGE);
        public static final IElementType ITEM_ATTRIBUTE_VALUE = new IElementType(KEY_ITEM + "_ATTRIBUTE_VALUE", LANGUAGE);
        public static final IElementType ITEM_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType ITEM_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType ITEM_DESTNAME = new IElementType(KEY_ITEM + "_DESTNAME", LANGUAGE);
        public static final IElementType ITEM_DESTNAME_VALUE = new IElementType(KEY_ITEM + "_DESTNAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_EXCLUDES = new IElementType(KEY_ITEM + "_EXCLUDES", LANGUAGE);
        public static final IElementType ITEM_EXCLUDES_VALUE = new IElementType(KEY_ITEM + "_EXCLUDES_VALUE", LANGUAGE);
        public static final IElementType ITEM_EXTERNALSIZE = new IElementType(KEY_ITEM + "_EXTERNALSIZE", LANGUAGE);
        public static final IElementType ITEM_EXTERNALSIZE_VALUE = new IElementType(KEY_ITEM + "_EXTERNAL_SIZE_VALUE", LANGUAGE);
        public static final IElementType ITEM_FONTINSTALL = new IElementType(KEY_ITEM + "_FONTINSTALL", LANGUAGE);
        public static final IElementType ITEM_FONTINSTALL_VALUE = new IElementType(KEY_ITEM + "_FONTINSTALL_VALUE", LANGUAGE);
        public static final IElementType ITEM_STRONGASSEMBLYNAME = new IElementType(KEY_ITEM + "_STRONGASSEMBLYNAME", LANGUAGE);
        public static final IElementType ITEM_STRONGASSEMBLYNAME_VALUE = new IElementType(KEY_ITEM + "_STRONGASSEMBLYNAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType ITEM_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType ITEM_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType ITEM_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class DirectorySection {
        private static final String KEY = "DIRECTORY_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType ITEM_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType ITEM_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_ATTRIBUTE = new IElementType(KEY_ITEM + "_ATTRIBUTE", LANGUAGE);
        public static final IElementType ITEM_ATTRIBUTE_VALUE = new IElementType(KEY_ITEM + "_ATTRIBUTE_VALUE", LANGUAGE);
        public static final IElementType ITEM_PERMISSIONS = new IElementType(KEY_ITEM + "_PERMISSIONS", LANGUAGE);
        public static final IElementType ITEM_PERMISSIONS_VALUE = new IElementType(KEY_ITEM + "_PERMISSIONS_VALUE", LANGUAGE);
        public static final IElementType ITEM_TASKS = new IElementType(KEY_ITEM + "_TASKS", LANGUAGE);
        public static final IElementType ITEM_TASKS_VALUE = new IElementType(KEY_ITEM + "_TASKS_VALUE", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS = new IElementType(KEY_ITEM + "_COMPONENTS", LANGUAGE);
        public static final IElementType ITEM_COMPONENTS_VALUE = new IElementType(KEY_ITEM + "_COMPONENTS_VALUE", LANGUAGE);
        public static final IElementType ITEM_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType ITEM_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class ComponentSection {
        private static final String KEY = "COMPONENT_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType ITEM_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType ITEM_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType ITEM_EXTRADISKSPACEREQUIRED = new IElementType(KEY_ITEM + "_EXTRADISKSPACEREQUIRED", LANGUAGE);
        public static final IElementType ITEM_EXTRADISKSPACEREQUIRED_VALUE = new IElementType(KEY_ITEM + "_EXTRADISKSPACEREQUIRED_VALUE", LANGUAGE);
        public static final IElementType ITEM_TYPES = new IElementType(KEY_ITEM + "_TYPES", LANGUAGE);
        public static final IElementType ITEM_TYPES_VALUE = new IElementType(KEY_ITEM + "_TYPES_VALUE", LANGUAGE);
        public static final IElementType ITEM_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType ITEM_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    public static final class TypeSection {
        private static final String KEY = "TYPE_SECTION";
        private static final String KEY_ITEM = KEY + "_ITEM";

        public static final IElementType SECTION = new IElementType(KEY, LANGUAGE);
        public static final IElementType SECTION_DEFINITION = new IElementType(KEY + "_DEFINITION", LANGUAGE);
        public static final IElementType ITEM_NAME = new IElementType(KEY_ITEM + "_NAME", LANGUAGE);
        public static final IElementType ITEM_NAME_VALUE = new IElementType(KEY_ITEM + "_NAME_VALUE", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION = new IElementType(KEY_ITEM + "_DESCRIPTION", LANGUAGE);
        public static final IElementType ITEM_DESCRIPTION_VALUE = new IElementType(KEY_ITEM + "_DESCRIPTION_VALUE", LANGUAGE);
        public static final IElementType ITEM_FLAGS = new IElementType(KEY_ITEM + "_FLAGS", LANGUAGE);
        public static final IElementType ITEM_FLAGS_VALUE = new IElementType(KEY_ITEM + "_FLAGS_VALUE", LANGUAGE);
    }

    private IssMarkerFactory() {
    }
}
