package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssSymbolValue implements Serializable {
    private String name;
    private String value;

    public IssSymbolValue() {
    }

    public IssSymbolValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssSymbolValue that = (IssSymbolValue) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "<" + name + "=" + value + ">";
    }
}
