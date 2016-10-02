package org.pcsoft.plugins.intellij.iss.language.type;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface SectionValue extends SectionBase {
    String getName();
    ValueType getValueType();
    Object getDefaultValue();
}
