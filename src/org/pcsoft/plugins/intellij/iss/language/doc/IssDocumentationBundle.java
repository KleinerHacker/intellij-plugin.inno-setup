package org.pcsoft.plugins.intellij.iss.language.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class IssDocumentationBundle {
    private static final Map<String, ResourceBundle> RESOURCE_BUNDLE_SECTION_MAP = new HashMap<>();
    private static final ResourceBundle RESOURCE_BUNDLE_SECTION_TITLE = ResourceBundle.getBundle("/messages/documentation_section");

    static {
        RESOURCE_BUNDLE_SECTION_MAP.put("setup", ResourceBundle.getBundle("/messages/documentation_setup"));
        RESOURCE_BUNDLE_SECTION_MAP.put("files", ResourceBundle.getBundle("/messages/documentation_file"));
        RESOURCE_BUNDLE_SECTION_MAP.put("dirs", ResourceBundle.getBundle("/messages/documentation_directory"));
        RESOURCE_BUNDLE_SECTION_MAP.put("types", ResourceBundle.getBundle("/messages/documentation_type"));
        RESOURCE_BUNDLE_SECTION_MAP.put("components", ResourceBundle.getBundle("/messages/documentation_component"));
        RESOURCE_BUNDLE_SECTION_MAP.put("tasks", ResourceBundle.getBundle("/messages/documentation_task"));
        RESOURCE_BUNDLE_SECTION_MAP.put("icons", ResourceBundle.getBundle("/messages/documentation_icon"));
        RESOURCE_BUNDLE_SECTION_MAP.put("ini", ResourceBundle.getBundle("/messages/documentation_ini"));
        RESOURCE_BUNDLE_SECTION_MAP.put("registry", ResourceBundle.getBundle("/messages/documentation_registry"));
    }

    public static ResourceBundle getSectionBundle(String sectionName) {
        return RESOURCE_BUNDLE_SECTION_MAP.get(sectionName.toLowerCase());
    }

    public static boolean containsSectionBundle(String sectionName) {
        return RESOURCE_BUNDLE_SECTION_MAP.containsKey(sectionName.toLowerCase());
    }

    public static ResourceBundle getSectionTitleBundle() {
        return RESOURCE_BUNDLE_SECTION_TITLE;
    }
}
