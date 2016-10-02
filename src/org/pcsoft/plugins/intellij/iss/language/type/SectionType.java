package org.pcsoft.plugins.intellij.iss.language.type;

import javax.swing.*;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface SectionType extends SectionBase {
    String getName();
    Icon getIcon();
    SectionVariant getVariant();
    Class<? extends SectionValue> getSectionValueClass();
}
