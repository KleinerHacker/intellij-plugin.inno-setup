package org.pcsoft.plugins.intellij.inno_setup.script.types.section;

import com.intellij.ui.JBColor;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPresentationHint;

import java.awt.Color;
import java.awt.Font;

/**
 * Created by Christoph on 21.11.2015.
 */
public enum IssSetupPropertyClassifier implements IssPresentationHint {
    Compiler("Compiler-related", Font.BOLD, JBColor.BLUE.brighter()),
    Installer("Installer-related", Font.BOLD,  JBColor.BLUE),
    Cosmetic("Cosmetic", Font.PLAIN, JBColor.GREEN),
    Obsolete("Obsolete", Font.PLAIN, JBColor.GRAY)
    ;

    private final String text;
    private final int fontStyle;
    private final Color textColor;

    private IssSetupPropertyClassifier(String text, int fontStyle, Color textColor) {
        this.text = text;
        this.fontStyle = fontStyle;
        this.textColor = textColor;
    }

    public String getText() {
        return text;
    }

    @Override
    public int getFontStyle() {
        return fontStyle;
    }

    @Override
    public Color getTextColor() {
        return textColor;
    }
}
