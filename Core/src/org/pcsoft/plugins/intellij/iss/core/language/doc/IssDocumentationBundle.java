package org.pcsoft.plugins.intellij.iss.core.language.doc;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class IssDocumentationBundle {
    private static final Map<String, ResourceBundle> RESOURCE_BUNDLE_PROPERTY_MAP = new HashMap<>();
    private static final ResourceBundle RESOURCE_BUNDLE_PROPERTY_COMMON = ResourceBundle.getBundle("/messages/documentation_common");
    private static final ResourceBundle RESOURCE_BUNDLE_SECTION = ResourceBundle.getBundle("/messages/documentation_section");
    private static final ResourceBundle RESOURCE_BUNDLE_CONSTANT = ResourceBundle.getBundle("/messages/documentation_constant");

    static {
        RESOURCE_BUNDLE_PROPERTY_MAP.put("setup", ResourceBundle.getBundle("/messages/documentation_setup"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("files", ResourceBundle.getBundle("/messages/documentation_file"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("dirs", ResourceBundle.getBundle("/messages/documentation_directory"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("types", ResourceBundle.getBundle("/messages/documentation_type"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("components", ResourceBundle.getBundle("/messages/documentation_component"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("tasks", ResourceBundle.getBundle("/messages/documentation_task"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("icons", ResourceBundle.getBundle("/messages/documentation_icon"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("ini", ResourceBundle.getBundle("/messages/documentation_ini"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("registry", ResourceBundle.getBundle("/messages/documentation_registry"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("run", ResourceBundle.getBundle("/messages/documentation_install_run"));
        RESOURCE_BUNDLE_PROPERTY_MAP.put("uninstallrun", ResourceBundle.getBundle("/messages/documentation_uninstall_run"));
    }

    public static ResourceBundle getPropertyBundle(String sectionName) {
        return RESOURCE_BUNDLE_PROPERTY_MAP.get(sectionName.toLowerCase());
    }

    public static boolean containsPropertyBundle(String sectionName) {
        return RESOURCE_BUNDLE_PROPERTY_MAP.containsKey(sectionName.toLowerCase());
    }

    public static ResourceBundle getSectionBundle() {
        return RESOURCE_BUNDLE_SECTION;
    }

    public static ResourceBundle getPropertyCommonBundle() {
        return RESOURCE_BUNDLE_PROPERTY_COMMON;
    }

    public static ResourceBundle getConstantBundle() {
        return RESOURCE_BUNDLE_CONSTANT;
    }
}
