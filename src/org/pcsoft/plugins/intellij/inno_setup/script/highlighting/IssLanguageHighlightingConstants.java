package org.pcsoft.plugins.intellij.inno_setup.script.highlighting;

import com.intellij.ui.JBColor;

import java.awt.Color;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public final class IssLanguageHighlightingConstants {

    public static final class Colors {
        public static final Color COMPILER = new JBColor(
                new Color(0x477F81),
                new Color(0x7DD5EA)
        );
        public static final Color SETUP = JBColor.BLUE;
        public static final Color MESSAGES = new JBColor(
                new Color(0x006F18),
                new Color(0x00A323)
        );
        public static final Color BUILTIN = new JBColor(
                new Color(0xCE5C60),
                new Color(0xE8A8AA)
        );
        public static final Color FAILURE = JBColor.RED;
        public static final Color SYSTEM = new JBColor(
                new Color(0x958526),
                new Color(0xE3CA3A)
        );
        public static final Color EXTERNAL = new JBColor(
                new Color(0x009685),
                new Color(0x76B79C)
        );
        public static final Color IGNORABLE = JBColor.GRAY;
        public static final Color VALUE = JBColor.BLUE.darker();
        public static final Color OPERATOR = JBColor.BLACK;
        public static final Color DESIGN = new JBColor(
                new Color(0xBC0072),
                new Color(0xE36FB8)
        );
        public static final Color LINK = JBColor.RED.darker();
    }

    private IssLanguageHighlightingConstants() {
    }
}
