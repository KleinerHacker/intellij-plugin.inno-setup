package org.pcsoft.plugins.intellij.inno_setup.script.highlighting;

import com.intellij.ui.JBColor;

import java.awt.Color;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public final class IssLanguageHighlightingConstants {

    public static final class Colors {
        public static final Color COMPILER = JBColor.BLUE.brighter();
        public static final Color SETUP = JBColor.BLUE;
        public static final Color MESSAGES = JBColor.GREEN;
        public static final Color BUILTIN = JBColor.PINK;
        public static final Color FAILURE = JBColor.RED;
        public static final Color SYSTEM = JBColor.YELLOW;
        public static final Color EXTERN = JBColor.CYAN.brighter();
        public static final Color IGNORABLE = JBColor.GRAY;
        public static final Color VALUE = JBColor.BLUE.darker();
        public static final Color OPERATOR = JBColor.BLACK;
        public static final Color DESIGN = JBColor.MAGENTA;
        public static final Color LINK = JBColor.RED.darker();
    }

    private IssLanguageHighlightingConstants() {
    }
}
