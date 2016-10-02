package org.pcsoft.plugins.intellij.iss;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by Christoph on 30.09.2016.
 */
public final class IssIcons {
    public static final Icon FILE = IconLoader.getIcon("/icons/is_script.png");

    public static final class Sections {
        public static final Icon Setup = IconLoader.getIcon("/icons/ic_sect_setup.png");
    }

    private IssIcons() {
    }
}
