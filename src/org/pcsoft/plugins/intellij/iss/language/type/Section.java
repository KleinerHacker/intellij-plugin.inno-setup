package org.pcsoft.plugins.intellij.iss.language.type;

import org.pcsoft.plugins.intellij.iss.IssIcons;

import javax.swing.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public enum Section implements SectionType {
    SETUP("SETUP", IssIcons.Sections.Setup, SectionVariant.ValueBased, SectionSetup.class, true);

    private final String name;
    private final Icon icon;
    private final SectionVariant variant;
    private final Class<? extends SectionValue> sectionValueClass;
    private final boolean required, deprecated;

    private Section(String name, Icon icon, SectionVariant variant, Class<? extends SectionValue> sectionValueClass) {
        this(name, icon, variant, sectionValueClass, false);
    }

    private Section(String name, Icon icon, SectionVariant variant, Class<? extends SectionValue> sectionValueClass, boolean required) {
        this(name, icon, variant, sectionValueClass, required, false);
    }

    private Section(String name, Icon icon, SectionVariant variant, Class<? extends SectionValue> sectionValueClass, boolean required, boolean deprecated) {
        this.name = name;
        this.icon = icon;
        this.variant = variant;
        this.sectionValueClass = sectionValueClass;
        this.required = required;
        this.deprecated = deprecated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public SectionVariant getVariant() {
        return variant;
    }

    @Override
    public Class<? extends SectionValue> getSectionValueClass() {
        return sectionValueClass;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}
