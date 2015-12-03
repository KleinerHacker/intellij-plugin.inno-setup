package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import com.intellij.ui.JBColor;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPresentationHint;

import java.awt.Color;
import java.awt.Font;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public enum IssBuiltinConstantType implements IssPresentationHint {
    Directory("Directory", Font.BOLD, JBColor.PINK),
    ShellFolder("Shell-Folder", Font.ITALIC, JBColor.PINK),
    Other(null, Font.PLAIN, JBColor.PINK);

    private final String text;
    private final int fontStyle;
    private final Color textColor;

    private IssBuiltinConstantType(String text, int fontStyle, Color textColor) {
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
