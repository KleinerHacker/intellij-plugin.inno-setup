package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.Color;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public interface IssPresentationHint {
    static TextAttributes buildFrom(IssPresentationHint presentationHint, boolean deprecated) {
        return new TextAttributes(
                presentationHint.getTextColor(), null, presentationHint.getTextColor(),
                deprecated ? EffectType.STRIKEOUT : null, presentationHint.getFontStyle()
        );
    }

    int getFontStyle();

    Color getTextColor();
}
