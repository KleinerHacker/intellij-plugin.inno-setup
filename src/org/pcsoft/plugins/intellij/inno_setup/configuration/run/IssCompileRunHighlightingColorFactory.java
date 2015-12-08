package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

/**
 * Created by Christoph on 20.11.2015.
 */
public final class IssCompileRunHighlightingColorFactory {

    public static final TextAttributesKey CONSOLE_OUT_IDE = TextAttributesKey.createTextAttributesKey(
            "CONSOLE_OUT_IDE",
            new TextAttributes(JBColor.BLUE, null, null, null, 0)
    );

    public static final TextAttributesKey CONSOLE_OUT_ERROR = TextAttributesKey.createTextAttributesKey(
            "CONSOLE_OUT_IDE",
            new TextAttributes(JBColor.RED, null, null, null, 0)
    );

    public static final TextAttributesKey CONSOLE_OUT_ERROR_LINK = TextAttributesKey.createTextAttributesKey(
            "CONSOLE_OUT_IDE",
            new TextAttributes(JBColor.RED, null, JBColor.RED, EffectType.LINE_UNDERSCORE, 0)
    );

    private IssCompileRunHighlightingColorFactory() {
    }
}
